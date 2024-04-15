package io.github.lanicc.binlog.core.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2024/4/15.
 *
 * @author lan
 */
//@VisibleForTesting
public class EventMockUtil {

    public static Event mockEvent(String database, String tableName, String binlogFilename, Long binlogPosition,
                                  Long timestamp, Event.Type type,
                                  List<Map<String, Serializable>> data, List<Map<String, Serializable>> old) {
        return new Event(database, tableName, binlogFilename, binlogPosition, timestamp, type, data, old);
    }

    public static Event mockEvent() {
        List<Map<String, Serializable>> data = new ArrayList<>();
        Map<String, Serializable> d = new HashMap<>();
        d.put("id", 1);
        d.put("name", "lan");
        d.put("age", 18);
        d.put("sex", "男");
        d.put("address", "北京");
        d.put("create_time", System.currentTimeMillis());
        d.put("update_time", System.currentTimeMillis());
        d.put("is_deleted", 0);
        d.put("version", 1);
        d.put("remark", "备注");
        d.put("create_user", "admin");
        d.put("update_user", "admin");
        d.put("create_ip", "127.0.0.1");
        d.put("update_ip", "127.0.0.1");
        data.add(d);
        return mockEvent("database", "tableName", "mysql-bin.00007", 1L, System.currentTimeMillis(), Event.Type.INSERT, data, null);
    }
}
