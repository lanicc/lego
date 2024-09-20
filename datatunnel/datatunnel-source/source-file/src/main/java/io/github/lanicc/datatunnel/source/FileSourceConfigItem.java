package io.github.lanicc.datatunnel.source;

import io.github.lanicc.datatunnel.config.ConfigItem;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public class FileSourceConfigItem {

    public static ConfigItem path = new ConfigItem("path", "", "监听此文件夹下的文件变动");
}
