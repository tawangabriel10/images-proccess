package br.com.bix.images.api.stream.consumer;

import static br.com.bix.images.api.stream.consumer.ProcessImageConsumerConfig.CONTAINER_FACTORY_BEAN_NAME;
import static br.com.bix.images.config.kafka.KafkaConfig.PROCESS_IMAGE_TOPIC;

import br.com.bix.images.api.stream.model.ProcessImageEvent;
import br.com.bix.images.service.ImageService;
import br.com.bix.images.service.ProcessImageService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessImageConsumer {

    private final ImageService imageService;

    @KafkaListener(topics = PROCESS_IMAGE_TOPIC,
        containerFactory = CONTAINER_FACTORY_BEAN_NAME)
    public void consume(@NonNull ProcessImageEvent event, @NonNull Acknowledgment acknowledgment) {
        acknowledgment.acknowledge();

        try {
            log.info("Consuming event on topic SAVE_ORDER_TOPIC value: {}", event);
            imageService.process(event);

        } catch(RuntimeException ex) {
            log.error("Failed to save Order by message: {}", ex.getMessage());
        }
    }
}
