package io.github.lanicc.binlog.connect;

import com.alibaba.fastjson.JSON;
import io.github.lanicc.binlog.core.event.AbstractEventListener;
import io.github.lanicc.binlog.core.event.Event;
import io.github.lanicc.binlog.core.util.PropertiesUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.Properties;

/**
 * Created on 2024/4/11.
 *
 * @author lan
 */
@SuppressWarnings("SpellCheckingInspection")
public class RocketMqConnectListener extends AbstractEventListener {
    DefaultMQProducer producer;
    String topic;

    public RocketMqConnectListener(String destination) {
        super(destination);
    }

    @Override
    protected void doInit(Properties properties) {
        String producerGroup = PropertiesUtil.getString(properties, "rocketmq.producer.group", destination);
        topic = PropertiesUtil.get(properties, "rocketmq.producer.topic", destination);
        log.info("rocketmq producer group: {}, topic: {}", producerGroup, topic);

        producer = new DefaultMQProducer(producerGroup);
        String namesrv = PropertiesUtil.getString(properties, "rocketmq.namesrv.addr", "127.0.0.1:9876");
        log.info("rocketmq namesrv addr: {}", namesrv);
        producer.setNamesrvAddr(namesrv);
    }

    @Override
    protected void doStart() {
        try {
            producer.start();
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public void onEvent(Event event) {
        Message message =
                new Message(
                        topic,
                        StringUtils.joinWith(",", destination, event.getDatabase(), event.getTablename()),
                        JSON.toJSONBytes(event)
                );
        producer.send(
                message,
                new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        if (log.isDebugEnabled()) {
                            log.debug("event: {}, send success: {}", event, sendResult);
                        }
                    }

                    @Override
                    public void onException(Throwable e) {
                        log.error("event: {}, send error", event, e);
                    }
                }
        );
    }
}
