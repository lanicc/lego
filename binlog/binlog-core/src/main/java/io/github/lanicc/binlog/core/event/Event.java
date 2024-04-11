package io.github.lanicc.binlog.core.event;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
@Data
public class Event {

    private String database;
    private String tablename;
    private String binlogFilename;
    private Long binlogPosition;
    private Long timestamp;

    private Type type;

    private List<Map<String, Serializable>> data;
    private List<Map<String, Serializable>> old;

    public Event() {
    }

    public Event(String database, String tablename,
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
