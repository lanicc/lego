package io.github.lanicc.datatunnel.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public class Config extends HashMap<String, Object> implements Serializable {

    public Config() {
    }

    public Config(Properties properties) {
        properties.forEach((key, value) -> this.put(key.toString(), value.toString()));
    }


    public String getProperty(String key) {
        Object o = get(key);
        if (Objects.isNull(o)) {
            return null;
        }
        return o.toString();
    }

    public String getProperty(ConfigItem item) {
        return getProperty(item.getKey(), item.getDefaultValue());
    }

    public String getProperty(String key, String defaultValue) {
        Object o = get(key);
        if (Objects.isNull(o)) {
            return defaultValue;
        }
        return o.toString();
    }

    public Map<String, String> getAsMap(ConfigItem item) {
        //noinspection unchecked
        return (Map<String, String>) get(item.getKey());
    }

    public int getAsInt(ConfigItem item) {
        Object o = get(item.getKey());
        if (Objects.isNull(o)) {
            if (Objects.isNull(item.getDefaultValue()) || item.getDefaultValue().isEmpty()) {
                return 0;
            }
            return Integer.parseInt(item.getDefaultValue());
        }
        return Integer.parseInt(o.toString());
    }

    public long getAsLong(ConfigItem item) {
        Object o = get(item.getKey());
        if (Objects.isNull(o)) {
            if (Objects.isNull(item.getDefaultValue()) || item.getDefaultValue().isEmpty()) {
                return 0;
            }
            return Long.parseLong(item.getDefaultValue());
        }
        return Long.parseLong(o.toString());
    }

    public boolean getAsBoolean(ConfigItem item) {
        Object o = get(item.getKey());
        if (Objects.isNull(o)) {
            if (Objects.isNull(item.getDefaultValue()) || item.getDefaultValue().isEmpty()) {
                return false;
            }
            return Boolean.parseBoolean(item.getDefaultValue());
        }
        return Boolean.parseBoolean(o.toString());
    }

}
