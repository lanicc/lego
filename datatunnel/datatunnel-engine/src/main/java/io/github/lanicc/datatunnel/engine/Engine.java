package io.github.lanicc.datatunnel.engine;

import io.github.lanicc.datatunnel.engine.service.CommitLogManager;
import io.github.lanicc.datatunnel.engine.service.HaManager;
import io.github.lanicc.datatunnel.engine.service.PluginManager;
import io.github.lanicc.datatunnel.engine.service.zk.ZkCommitLogManager;
import io.github.lanicc.datatunnel.engine.service.zk.ZkHaManager;
import io.github.lanicc.datatunnel.engine.task.TaskConfig;
import io.github.lanicc.datatunnel.engine.task.TaskInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@Slf4j
public class Engine {

    private final PluginManager pluginManager = new PluginManager();
    private final CommitLogManager commitLogManager;
    private final HaManager haManager;

    private final Map<String, TaskConfig> taskConfigs;
    private final Map<String, TaskInstance> taskInstances;

    private final HaControl haControl;

    public Engine(String zk) {
        CuratorFramework curatorClient = CuratorFrameworkFactory.newClient(zk, new ExponentialBackoffRetry(1000, 3));
        curatorClient.start();
        commitLogManager = new ZkCommitLogManager(curatorClient);
        haManager = new ZkHaManager(curatorClient);
        taskConfigs = new ConcurrentHashMap<>();
        taskInstances = new ConcurrentHashMap<>();
        haControl = new HaControl();
    }

    public void start(TaskConfig taskConfig) throws Exception {
        taskConfigs.put(taskConfig.getName(), taskConfig);
        haManager.register(taskConfig.getName(), haControl);
    }

    public void stop(String task) throws Exception {
        haControl.stop(task);
    }

    private void start0(String task) throws Exception {
        try {
            stop0(task);
        } catch (Exception e) {
            // ignore
        }
        log.info("Start: {}", task);
        TaskConfig taskConfig = taskConfigs.get(task);
        if (Objects.isNull(taskConfig)) {
            throw new RuntimeException("No config");
        }
        TaskInstance taskInstance = new TaskInstance(taskConfig, pluginManager, commitLogManager);
        taskInstance.start();
        taskInstances.put(task, taskInstance);
    }

    private void stop0(String task) {
        log.info("Stop: {}", task);
        TaskInstance removed = taskInstances.remove(task);
        if (Objects.nonNull(removed)) {
            try {
                removed.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public TaskConfig load(String configFile) throws IOException {
        return new Yaml().loadAs(Files.newInputStream(Paths.get(configFile)), TaskConfig.class);
    }

    class HaControl implements HaManager.HaControl {

        @Override
        public void start(String taskName) throws Exception {
            Engine.this.start0(taskName);
        }

        @Override
        public void stop(String taskName) {
            Engine.this.stop0(taskName);
        }
    }
}
