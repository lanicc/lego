package io.github.lanicc.binlog.core.meta;

import io.github.lanicc.binlog.core.AbstractLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
public class AbstractMetaManager extends AbstractLifeCycle implements MetaManager {

    protected final String destination;
    protected final Logger log;

    protected final Map<Long, TableMeta> tableMetaMap;

    public AbstractMetaManager(String destination) {
        this.destination = destination;
        this.tableMetaMap = new ConcurrentHashMap<>();
        this.log = LoggerFactory.getLogger(destination);
    }

    @Override
    public Position getCursor() {
        return null;
    }

    @Override
    public void rotate(String binlogFilename, Long binlogPosition) {

    }

    @Override
    public void updateCursor(Long binlogPosition) {

    }

    @Override
    public void putTableMeta(Long tableId, String database, String table) {
        tableMetaMap.put(tableId, new TableMeta(database, table, getTableColumns(database, table)));
    }

    @Override
    public TableMeta getTableMeta(Long tableId) {
        return tableMetaMap.get(tableId);
    }

    protected List<String> getTableColumns(String database, String table) {
        return Collections.emptyList();
    }
}
