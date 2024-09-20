package io.github.lanicc.datatunnel.source;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@Data
public class CdcEvent {

    private String database;
    private String tablename;
    private String binlogFilename;
    private Long binlogPosition;
    private Long timestamp;

    private Type type;

    private List<Map<String, Serializable>> data;
    private List<Map<String, Serializable>> old;

    public CdcEvent() {
    }

    public CdcEvent(String database, String tablename,
                 String binlogFilename, Long binlogPosition,
                 Long timestamp, Type type,
                 List<Map<String, Serializable>> data, List<Map<String, Serializable>> old) {
        this.database = database;
        this.tablename = tablename;
        this.binlogFilename = binlogFilename;
        this.binlogPosition = binlogPosition;
        this.timestamp = timestamp;
        this.type = type;
        this.data = data;
        this.old = old;
    }

    public enum Type {
        INSERT,
        UPDATE,
        DELETE,

    }
}
