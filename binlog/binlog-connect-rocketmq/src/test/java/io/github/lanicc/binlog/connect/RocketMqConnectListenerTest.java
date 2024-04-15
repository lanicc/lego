package io.github.lanicc.binlog.connect;

import io.github.lanicc.binlog.core.event.Event;
import io.github.lanicc.binlog.core.event.EventMockUtil;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/4/15.
 *
 * @author lan
 */
class RocketMqConnectListenerTest {

    @Test
    void doStart() throws InterruptedException {
        RocketMqConnectListener listener = new RocketMqConnectListener("test");
        Properties properties = new Properties();
        listener.init(properties);
        listener.start();

        Event event = EventMockUtil.mockEvent();
        listener.onEvent(event);
        TimeUnit.SECONDS.sleep(5);
    }
}
