package io.github.lanicc.binlog.core;

import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created on 2024/4/11.
 *
 * @author lan
 */
class TaskConfigMonitorTest {

    @Test
    void doStart() throws InterruptedException {
        TaskConfigMonitor monitor = new TaskConfigMonitor(new TaskController());
        Properties properties = new Properties();
        properties.setProperty("destinations", "src/test/resources/destinations");
        monitor.init(properties);
        monitor.start();
        TimeUnit.HOURS.sleep(20);
    }
}
