package io.github.lanicc.binlog.connect;

import com.alibaba.fastjson.JSON;
import io.github.lanicc.binlog.core.event.AbstractEventListener;
import io.github.lanicc.binlog.core.event.Event;
import io.github.lanicc.binlog.core.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Created on 2024/4/15.
 *
 * @author lan
 */
public class KafkaConnectListener extends AbstractEventListener {
    KafkaProducer<String, String> producer;
    String topic;

    public KafkaConnectListener(String destination) {
        super(destination);
    }

    @Override
    protected void doInit(Properties properties) {
        topic = PropertiesUtil.getString(properties, "topic", destination);
        log.info("topic: {}", topic);
        StringSerializer stringSerializer = new StringSerializer();
        producer = new KafkaProducer<>(properties, stringSerializer, stringSerializer);
    }

    @Override
    public void onEvent(Event event) {
        producer.send(
                new ProducerRecord<>(
                        topic,
                        JSON.toJSONString(event),
                        StringUtils.joinWith(".", destination, event.getDatabase(), event.getTablename())
                ),
                (metadata, exception) -> {
                    if (exception != null) {
                        log.error("event: {}, send kafka error", event, exception);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("event: {}, send kafka success, metadata: {}", event, metadata);
                        }
                    }
                }
        );
    }

    @Override
    protected void doStop() {
        producer.close();
    }
}
