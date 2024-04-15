package io.github.lanicc.binlog.core.meta;

import io.github.lanicc.binlog.core.LifeCycle;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
public interface MetaManager extends LifeCycle {

    Position getCursor();

    default String getBinlogFilename() {
        Position position = getCursor();
        if (position == null) {
            return null;
        }
        return position.getBinlogFilename();
    }

    void rotate(String binlogFilename, Long binlogPosition);

    void updateCursor(Long binlogPosition);

    void putTableMeta(Long tableId, String database, String table);

    TableMeta getTableMeta(Long tableId);
}
