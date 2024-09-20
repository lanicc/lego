package io.github.lanicc.datatunnel.engine;

import io.github.lanicc.datatunnel.engine.task.TaskConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@Slf4j
class EngineTest {

    Engine engine = new Engine("localhost:2181");

    @Test
    @Timeout(value = 100)
    void start() throws Exception {
        startTask("/Users/lan/IdeaProjects/github/lego/datatunnel/datatunnel-engine/src/test/resources/test1.yml");
    }

    void startTask(String configFile) throws Exception {
        TaskConfig taskConfig = engine.load(configFile);
        Assertions.assertNotNull(taskConfig);
        log.info(taskConfig.toString());
        engine.start(taskConfig);
        TimeUnit.SECONDS.sleep(60);
        engine.stop(taskConfig.getName());
    }
}
