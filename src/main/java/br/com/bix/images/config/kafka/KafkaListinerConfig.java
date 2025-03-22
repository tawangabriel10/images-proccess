package br.com.bix.images.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.LogIfLevelEnabled.Level;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class KafkaListinerConfig<E> {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private ObjectMapper objectMapper;

    protected ConcurrentKafkaListenerContainerFactory<String, E> createKafkaListenerContainerFactory(Class<E> clazz) {
        int maxPollRecords = 10;
        ConcurrentKafkaListenerContainerFactory<String, E> factory = new ConcurrentKafkaListenerContainerFactory();
        Map<String, Object> props = new HashMap();
        props.put("enable.auto.commit", false);
        props.put("request.timeout.ms", 30000);
        props.put("heartbeat.interval.ms", 1000);
        props.put("max.poll.interval.ms", 900000);
        props.put("max.poll.records", maxPollRecords);
        props.put("session.timeout.ms", 30000);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setSyncCommits(true);
        factory.setConsumerFactory(this.buildConsumerFactory(clazz, props));
        factory.getContainerProperties().setCommitLogLevel(Level.INFO);
        factory.setAckDiscarded(true);
        return factory;
    }

    private ConsumerFactory<String, E> buildConsumerFactory(Class<E> clazz, Map<String, Object> extraProps) {
        Map<String, Object> props = new HashMap();
        props.put("bootstrap.servers", bootstrapServer);
        props.put("group.id", applicationName);
        props.putAll(extraProps);
        StringDeserializer keyDeserializer = new StringDeserializer();
        JsonDeserializer<E> valueDeserializer = new JsonDeserializer(clazz, this.objectMapper);
        return new DefaultKafkaConsumerFactory(props, keyDeserializer, valueDeserializer);
    }
}
