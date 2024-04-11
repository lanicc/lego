package io.github.lanicc.binlog.core;

import io.github.lanicc.binlog.core.task.TaskConfig;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
class TaskControllerTest {

    @Test
    void load() throws InterruptedException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("example.yml");
        TaskConfig taskConfig = new Yaml().loadAs(inputStream, TaskConfig.class);
        new TaskController().load(taskConfig);
        TimeUnit.SECONDS.sleep(20);
    }
}
