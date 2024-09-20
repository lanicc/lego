package io.github.lanicc.datatunnel.engine;

import io.github.lanicc.datatunnel.engine.config.DatatunnelConfig;
import io.github.lanicc.datatunnel.engine.config.DatatunnelConfigLoader;
import io.github.lanicc.datatunnel.engine.service.TaskConfigManager;
import io.github.lanicc.datatunnel.engine.task.TaskConfig;

/**
 * Created on 2024/9/19.
 *
 * @author lan
 */
public class Bootstrap {

    public static void main(String[] args) {
        DatatunnelConfig datatunnelConfig = DatatunnelConfigLoader.getDatatunnelConfig();
        String taskConfigPath = datatunnelConfig.getTaskConfigPath();
        Engine engine = new Engine(datatunnelConfig.getZkUrl());
        TaskConfigManager.TaskConfigListener taskConfigListener =
                new TaskConfigManager.TaskConfigListener() {
                    @Override
                    public void load(TaskConfig taskConfig) throws Exception {
                        engine.start(taskConfig);
                    }

                    @Override
                    public void unload(String task) throws Exception {
                        engine.stop(task);
                    }
                };
        new TaskConfigManager(taskConfigPath, taskConfigListener).start();
    }
}
