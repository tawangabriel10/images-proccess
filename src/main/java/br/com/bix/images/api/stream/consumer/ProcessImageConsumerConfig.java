package br.com.bix.images.api.stream.consumer;

import br.com.bix.images.api.stream.model.ProcessImageEvent;
import br.com.bix.images.api.stream.model.SaveImageEvent;
import br.com.bix.images.config.kafka.KafkaListinerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class ProcessImageConsumerConfig extends KafkaListinerConfig<ProcessImageEvent> {

    static final String CONTAINER_FACTORY_BEAN_NAME = "processImageContainerFactory";

    @Bean(CONTAINER_FACTORY_BEAN_NAME)
    public ConcurrentKafkaListenerContainerFactory<String, ProcessImageEvent> containerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProcessImageEvent> factory = createKafkaListenerContainerFactory(
            ProcessImageEvent.class);
        factory.setAckDiscarded(true);
        return factory;
    }

}
