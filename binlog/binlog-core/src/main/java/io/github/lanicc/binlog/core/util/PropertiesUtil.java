package io.github.lanicc.binlog.core.util;

import java.util.Objects;
import java.util.Properties;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
public final class PropertiesUtil {

    public static String getString(Properties properties, String key, String defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : value;
    }

    public static int getInt(Properties properties, String key, int defaultValue) {
        Object o = properties.get(key);
        if (o instanceof Integer) {
            return (int) o;
        }
        String value = Objects.toString(o, null);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    public static boolean getBool(Properties properties, String key, boolean defaultValue) {
        Object o = properties.get(key);
        if (o instanceof Boolean) {
            return (boolean) o;
        }
        String value = Objects.toString(o, null);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public static long getLong(Properties properties, String key, long defaultValue) {
        Object o = properties.get(key);
        if (o instanceof Long) {
            return (long) o;
        }
        String value = Objects.toString(o, null);
        return value == null ? defaultValue : Long.parseLong(value);
    }
}
