package io.github.lanicc.binlog.core.reader;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import io.github.lanicc.binlog.core.LegoBinlogException;
import io.github.lanicc.binlog.core.event.EventListener;
import io.github.lanicc.binlog.core.meta.MetaManager;
import io.github.lanicc.binlog.core.meta.Position;
import io.github.lanicc.binlog.core.meta.TableMeta;
import io.github.lanicc.binlog.core.util.PropertiesUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
public class BinlogClientReader extends AbstractBinlogReader {

    private final Map<Long, TableMeta> tableMetaMap = new ConcurrentHashMap<>();
    private BinaryLogClient binaryLogClient;

    public BinlogClientReader(String destination, MetaManager metaManager, EventListener eventListener) {
        super(destination, metaManager, eventListener);
    }

    @Override
    protected void doInit(Properties properties) {
        String hostname = PropertiesUtil.getString(properties, "hostname", "localhost");
        int port = PropertiesUtil.getInt(properties, "port", 3306);
        String username = PropertiesUtil.getString(properties, "username", "root");
        String password = PropertiesUtil.getString(properties, "password", "root");
        binaryLogClient = new BinaryLogClient(hostname, port, username, password);

        binaryLogClient.setServerId(PropertiesUtil.getInt(properties, "serverId", 65535));
        binaryLogClient.setKeepAlive(PropertiesUtil.getBool(properties, "keepAlive", true));

        Position position = metaManager.getCursor();
        String binlogFilename = PropertiesUtil.getString(properties, "binlogFilename", null);
        long binlogPosition = PropertiesUtil.getLong(properties, "binlogPosition", 0L);
        if (StringUtils.isNotEmpty(binlogFilename) && Objects.nonNull(position)) {
            log.debug("Using position from metaManager");
            binlogFilename = position.getBinlogFilename();
            binlogPosition = position.getBinlogPosition();
        }
        log.info("Init with binlogFilename: {}, binlogPosition: {}", binlogFilename, binlogPosition);
        binaryLogClient.setBinlogFilename(binlogFilename);
        binaryLogClient.setBinlogPosition(binlogPosition);
        binaryLogClient.registerEventListener(new BinaryLogClientEventListener());
    }

    @Override
    protected void doStart() {
        try {
            binaryLogClient.connect();
        } catch (IOException e) {
            throw new LegoBinlogException(e);
        }
    }

    @Override
    protected void doStop() {
        try {
            binaryLogClient.disconnect();
        } catch (IOException e) {
            throw new LegoBinlogException(e);
        }
    }

    class BinaryLogClientEventListener implements BinaryLogClient.EventListener {

        @Override
        public void onEvent(Event event) {
            EventHeaderV4 header = event.getHeader();
            EventType eventType = header.getEventType();
            log.info("position: {}, nextPosition: {}, eventType: {}", header.getPosition(), header.getNextPosition(), eventType);
            if (log.isDebugEnabled()) {
                log.debug("position: {}, nextPosition: {}, eventType: {}, data: {}", header.getPosition(), header.getNextPosition(), eventType, event.getData());
            }

            if (Objects.equals(eventType, EventType.ROTATE)) {
                onRotate(event);
            } else {
                metaManager.updateCursor(header.getPosition());
                if (Objects.equals(eventType, EventType.TABLE_MAP)) {
                    onTableMap(event);
                } else if (EventType.isRowMutation(eventType)) {
                    onRowMutation(event);
                }
            }
        }

        private List<Map<String, Serializable>> fillColumnName(TableMeta tableMeta, List<Serializable[]> rows) {
            if (CollectionUtils.isEmpty(rows)) {
                return Collections.emptyList();
            }
            List<String> columns = tableMeta.getColumns();
            return
                    rows.stream()
                            .map(row -> {
                                Map<String, Serializable> data = new HashMap<>(row.length);
                                Iterator<String> iterator = columns.iterator();
                                for (int i = 0; i < row.length; i++) {
                                    String colName = iterator.hasNext() ? iterator.next() : "" + i;
                                    data.put(colName, row[i]);
                                }
                                return data;
                            })
                            .collect(Collectors.toList());
        }

        protected void onRotate(Event event) {
            RotateEventData data = event.getData();
            metaManager.rotate(data.getBinlogFilename(), data.getBinlogPosition());
        }

        protected void onRowMutation(Event event) {
            EventHeaderV4 header = event.getHeader();
            EventType eventType = header.getEventType();
            long timestamp = header.getTimestamp();
            io.github.lanicc.binlog.core.event.Event.Type type = null;
            long tableId = -1;
            List<Serializable[]> rows = Collections.emptyList();
            List<Serializable[]> beforeRows = Collections.emptyList();
            if (EventType.isWrite(eventType)) {
                WriteRowsEventData data = event.getData();
                tableId = data.getTableId();
                type = io.github.lanicc.binlog.core.event.Event.Type.INSERT;
                rows = data.getRows();
            } else if (EventType.isUpdate(eventType)) {
                UpdateRowsEventData data = event.getData();
                tableId = data.getTableId();
                type = io.github.lanicc.binlog.core.event.Event.Type.UPDATE;
                List<Map.Entry<Serializable[], Serializable[]>> entries = data.getRows();
                beforeRows = entries.stream()
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                rows = entries.stream()
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
            } else if (EventType.isDelete(eventType)) {
                DeleteRowsEventData data = event.getData();
                tableId = data.getTableId();
                type = io.github.lanicc.binlog.core.event.Event.Type.DELETE;
                beforeRows = data.getRows();
            }
            io.github.lanicc.binlog.core.event.Event myEvent =
                    mapToMyEvent(tableId, rows, beforeRows, type, metaManager.getBinlogFilename(), header.getPosition(), timestamp);
            if (myEvent != null) {
                eventListener.onEvent(myEvent);
            }
        }

        private io.github.lanicc.binlog.core.event.Event
        mapToMyEvent(long tableId, List<Serializable[]> rows,
                     List<Serializable[]> beforeRows,
                     io.github.lanicc.binlog.core.event.Event.Type type,
                     String binlogFilename, Long binlogPosition, long timestamp) {
            TableMeta tableMeta = getTableMeta(tableId);
            if (Objects.isNull(tableMeta)) {
                log.warn("cannot found table meta, table id: {}", tableId);
                return null;
            }
            List<Map<String, Serializable>> rowsData = fillColumnName(tableMeta, rows);
            List<Map<String, Serializable>> beforeRowsData = fillColumnName(tableMeta, beforeRows);
            return new io.github.lanicc.binlog.core.event.Event(
                    tableMeta.getDatabase(), tableMeta.getTablename(),
                    binlogFilename, binlogPosition,
                    timestamp, type,
                    rowsData, beforeRowsData
            );
        }

        protected TableMeta getTableMeta(long tableId) {
            return metaManager.getTableMeta(tableId);
        }

        protected void onTableMap(Event event) {
            TableMapEventData data = event.getData();
            metaManager.putTableMeta(data.getTableId(), data.getDatabase(), data.getTable());
        }

    }
}
