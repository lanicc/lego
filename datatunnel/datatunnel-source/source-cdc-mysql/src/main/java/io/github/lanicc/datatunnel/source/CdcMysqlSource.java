package io.github.lanicc.datatunnel.source;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import io.github.lanicc.datatunnel.CommitLog;
import io.github.lanicc.datatunnel.DataConsumer;
import io.github.lanicc.datatunnel.DataObject;
import io.github.lanicc.datatunnel.Source;
import io.github.lanicc.datatunnel.config.Config;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@Slf4j
public class CdcMysqlSource extends Thread implements Source {

    private final BinaryLogClient binaryLogClient;

    private final MetaManager metaManager;

    public CdcMysqlSource(Config config, CommitLog commitLog) throws Exception {
        String hostname = config.getProperty(CdcMysqlSourceConfigItem.hostname);
        int port = config.getAsInt(CdcMysqlSourceConfigItem.port);
        String username = config.getProperty(CdcMysqlSourceConfigItem.username);
        String password = config.getProperty(CdcMysqlSourceConfigItem.password);
        int serverId = config.getAsInt(CdcMysqlSourceConfigItem.serverId);
        boolean keepAlive = config.getAsBoolean(CdcMysqlSourceConfigItem.keepAlive);

        String binlogFilename = config.getProperty(CdcMysqlSourceConfigItem.binlogFilename);
        long binlogPosition = config.getAsLong(CdcMysqlSourceConfigItem.binlogPosition);

        metaManager = new MetaManager(commitLog);
        CommittedLog committedLog = metaManager.getCommittedLog();
        if (Objects.nonNull(committedLog)) {
            binlogFilename = committedLog.getBinlogFilename();
            binlogPosition = committedLog.getBinlogPosition();
        }
        binaryLogClient = new BinaryLogClient(hostname, port, username, password);
        binaryLogClient.setServerId(serverId);
        binaryLogClient.setKeepAlive(keepAlive);
        binaryLogClient.setBinlogFilename(binlogFilename);
        binaryLogClient.setBinlogPosition(binlogPosition);
    }

    @Override
    public void open(DataConsumer consumer) throws Exception {
        binaryLogClient.registerEventListener(new BinaryLogClientEventListener(consumer));
        start();
    }

    @Override
    public void run() {
        try {
            binaryLogClient.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        binaryLogClient.disconnect();
    }

    static class MetaManager {

        protected final Map<Long, TableMeta> tableMetaMap;
        private final CommitLog commitLog;
        @Getter
        private String binlogFilename;


        public MetaManager(CommitLog commitLog) {
            this.commitLog = commitLog;
            this.tableMetaMap = new HashMap<>();
        }

        public void rotate(String binlogFilename, Long binlogPosition) {
            this.binlogFilename = binlogFilename;
            try {
                commitLog.commit(new CommittedLog(binlogFilename, binlogPosition));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        public void updateCursor(Long binlogPosition) {
            try {
                commitLog.commit(new CommittedLog(binlogFilename, binlogPosition));
            } catch (Exception e) {
                log.error("update cursor error", e);
                throw new RuntimeException(e);
            }
        }

        public CommittedLog getCommittedLog() throws Exception {
            return commitLog.read();
        }

        public void putTableMeta(Long tableId, String database, String table) {
            tableMetaMap.put(tableId, new TableMeta(database, table, getTableColumns(database, table)));
        }

        public TableMeta getTableMeta(Long tableId) {
            return tableMetaMap.get(tableId);
        }

        protected List<String> getTableColumns(String database, String table) {
            return Collections.emptyList();
        }

    }

    class BinaryLogClientEventListener implements BinaryLogClient.EventListener {

        private final DataConsumer consumer;

        public BinaryLogClientEventListener(DataConsumer consumer) {
            this.consumer = consumer;
        }

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
            if (Objects.isNull(rows) || rows.isEmpty()) {
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
            CdcEvent.Type type = null;
            long tableId = -1;
            List<Serializable[]> rows = Collections.emptyList();
            List<Serializable[]> beforeRows = Collections.emptyList();
            if (EventType.isWrite(eventType)) {
                WriteRowsEventData data = event.getData();
                tableId = data.getTableId();
                type = CdcEvent.Type.INSERT;
                rows = data.getRows();
            } else if (EventType.isUpdate(eventType)) {
                UpdateRowsEventData data = event.getData();
                tableId = data.getTableId();
                type = CdcEvent.Type.UPDATE;
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
                type = CdcEvent.Type.DELETE;
                beforeRows = data.getRows();
            }
            CdcEvent myEvent =
                    mapToMyEvent(tableId, rows, beforeRows, type, metaManager.getBinlogFilename(), header.getPosition(), timestamp);
            if (myEvent != null) {
                DataObject dataObject = new DataObject();
                dataObject.put("d", myEvent);
                consumer.consume(dataObject);
            }
        }

        private CdcEvent mapToMyEvent(long tableId, List<Serializable[]> rows,
                     List<Serializable[]> beforeRows,
                     CdcEvent.Type type,
                     String binlogFilename, Long binlogPosition, long timestamp) {
            TableMeta tableMeta = getTableMeta(tableId);
            if (Objects.isNull(tableMeta)) {
                log.warn("cannot found table meta, table id: {}", tableId);
                return null;
            }
            List<Map<String, Serializable>> rowsData = fillColumnName(tableMeta, rows);
            List<Map<String, Serializable>> beforeRowsData = fillColumnName(tableMeta, beforeRows);
            return new CdcEvent(
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
