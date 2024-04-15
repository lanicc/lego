package io.github.lanicc.binlog.connect;

import io.github.lanicc.binlog.core.event.EventMockUtil;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/4/15.
 *
 * @author lan
 */
class KafkaConnectListenerTest {

    @Test
    void onEvent() throws InterruptedException {
        KafkaConnectListener listener = new KafkaConnectListener("test");
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("acks", "all");
        listener.init(properties);
        listener.start();

        listener.onEvent(EventMockUtil.mockEvent());
        TimeUnit.SECONDS.sleep(5);
        listener.stop();
    }
}
