package io.github.lanicc.datatunnel.engine.task;

import io.github.lanicc.datatunnel.config.Config;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@Data
@NoArgsConstructor
public class TaskConfig implements Serializable {

    private String name;

    private PluginConfig sourceConfig;

    private List<PluginConfig> transformConfigs;

    private PluginConfig sinkConfig;

    @Data
    @NoArgsConstructor
    public static class PluginConfig implements Serializable {

        private String pluginName;

        private String pluginType;

        private Config config;
    }
}
