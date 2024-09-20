package io.github.lanicc.datatunnel.source;

import io.github.lanicc.datatunnel.config.ConfigItem;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public class CdcMysqlSourceConfigItem {

    public static ConfigItem hostname = new ConfigItem("hostname", "localhost", "");
    public static ConfigItem port = new ConfigItem("port", "3306", "");
    public static ConfigItem username = new ConfigItem("username", "root", "");
    public static ConfigItem password = new ConfigItem("password", "root", "");
    public static ConfigItem serverId = new ConfigItem("serverId", "65535", "");
    public static ConfigItem keepAlive = new ConfigItem("keepAlive", "true", "");
    public static ConfigItem binlogFilename = new ConfigItem("binlogFilename", "", "");
    public static ConfigItem binlogPosition = new ConfigItem("binlogPosition", "", "");


}
