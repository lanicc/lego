package io.github.lanicc.datatunnel.engine.task;

import io.github.lanicc.datatunnel.*;
import io.github.lanicc.datatunnel.engine.service.CommitLogManager;
import io.github.lanicc.datatunnel.engine.service.PluginManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public class TaskInstance {

    private final TaskConfig taskConfig;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Sink sink;
    private Source source;
    private List<Transform> transforms;

    public TaskInstance(TaskConfig taskConfig,
                        PluginManager pluginManager,
                        CommitLogManager commitLogManager)
            throws Exception {
        this.taskConfig = taskConfig;
        init(pluginManager, commitLogManager);
    }

    private void init(PluginManager pluginManager, CommitLogManager commitLogManager) throws Exception {
        TaskConfig.PluginConfig sinkConfig = taskConfig.getSinkConfig();
        TaskConfig.PluginConfig sourceConfig = taskConfig.getSourceConfig();
        List<TaskConfig.PluginConfig> transformConfigs = taskConfig.getTransformConfigs();

        sink = pluginManager.getPlugin(SinkFactory.class, sinkConfig.getPluginName())
                .create(sinkConfig.getConfig(), commitLogManager.getCommitLog(taskConfig.getName(), sinkConfig.getPluginName()));
        source = pluginManager.getPlugin(SourceFactory.class, sourceConfig.getPluginName())
                .create(sourceConfig.getConfig(), commitLogManager.getCommitLog(taskConfig.getName(), sourceConfig.getPluginName()));

        transforms = new ArrayList<>();
        if (Objects.nonNull(transformConfigs)) {
            for (TaskConfig.PluginConfig transformConfig : transformConfigs) {
                Transform transform =
                        pluginManager.getPlugin(TransformFactory.class, transformConfig.getPluginName())
                                .create(transformConfig.getConfig(), commitLogManager.getCommitLog(taskConfig.getName(), transformConfig.getPluginName()));
                transforms.add(transform);
            }
        }

    }

    public void start() throws Exception {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        DataConsumer sinkDataConsumer = sink.open();
        DataConsumer taskDataConsumer =
                dataObject -> {
                    for (Transform transform : transforms) {
                        dataObject = transform.transform(dataObject);
                    }
                    sinkDataConsumer.consume(dataObject);
                };
        source.open(taskDataConsumer);
    }

    public void stop() throws Exception {
        if (!running.compareAndSet(true, false)) {
            source.close();
            sink.close();
        }
    }


}
