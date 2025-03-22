package br.com.bix.images.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    public static final String SAVE_IMAGE_TOPIC = "SAVE_IMAGE_TOPIC";
    public static final String PROCESS_IMAGE_TOPIC = "PROCESS_IMAGE_TOPIC";

    @Bean
    public NewTopic saveImageTopic() {
        return new NewTopic(SAVE_IMAGE_TOPIC, 6, (short)3);
    }

    @Bean
    public NewTopic processImageTopic() {
        return new NewTopic(PROCESS_IMAGE_TOPIC, 6, (short)3);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
