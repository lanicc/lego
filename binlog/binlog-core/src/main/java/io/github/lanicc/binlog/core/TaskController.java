package io.github.lanicc.binlog.core;

import io.github.lanicc.binlog.core.task.TaskConfig;
import io.github.lanicc.binlog.core.task.TaskInstance;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
public class TaskController {

    private final Map<String, TaskInstance> taskInstances = new ConcurrentHashMap<>();

    public void stop(String destination) {
        TaskInstance removed = taskInstances.remove(destination);
        if (removed != null) {
            removed.stop();
        }

    }

    public String load(TaskConfig config) {
        stop(config.getDestination());
        TaskInstance taskInstance = new TaskInstance(config);
        taskInstance.init(null);
        taskInstance.start();
        return config.getDestination();
    }

    public String loadFromFile(String file) throws IOException {
        return loadFromFile(new File(file));
    }

    public String loadFromFile(File file) throws IOException {
        return loadFromInputStream(Files.newInputStream(file.toPath()));
    }

    public String loadFromInputStream(InputStream inputStream) {
        return load(new Yaml().loadAs(inputStream, TaskConfig.class));
    }

}
