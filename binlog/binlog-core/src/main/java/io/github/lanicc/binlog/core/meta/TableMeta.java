package io.github.lanicc.binlog.core.meta;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Created on 2024/1/25.
 *
 * @author lan
 */
@Getter
public class TableMeta {

    private final String database;
    private final String tablename;

    private final List<String> columns;

    public TableMeta(String database, String tablename) {
        this.database = database;
        this.tablename = tablename;
        this.columns = Collections.emptyList();
    }

    public TableMeta(String database, String tablename, List<String> columns) {
        this.database = database;
        this.tablename = tablename;
        this.columns = columns;
    }

    @Override
    public String toString() {
        return database + "." + tablename + ": " + columns;
    }
}
