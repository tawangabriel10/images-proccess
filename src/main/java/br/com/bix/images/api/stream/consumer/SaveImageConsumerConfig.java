package br.com.bix.images.api.stream.consumer;

import br.com.bix.images.api.stream.model.SaveImageEvent;
import br.com.bix.images.config.kafka.KafkaListinerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class SaveImageConsumerConfig extends KafkaListinerConfig<SaveImageEvent> {

    static final String CONTAINER_FACTORY_BEAN_NAME = "saveImageContainerFactory";

    @Bean(CONTAINER_FACTORY_BEAN_NAME)
    public ConcurrentKafkaListenerContainerFactory<String, SaveImageEvent> containerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SaveImageEvent> factory = createKafkaListenerContainerFactory(
            SaveImageEvent.class);
        factory.setAckDiscarded(true);
        return factory;
    }
}
