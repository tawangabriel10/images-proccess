package br.com.bix.images.api.stream.consumer;

import static br.com.bix.images.api.stream.consumer.ProcessImageConsumerConfig.CONTAINER_FACTORY_BEAN_NAME;
import static br.com.bix.images.config.kafka.KafkaConfig.PROCESS_IMAGE_TOPIC;

import br.com.bix.images.api.stream.model.ProcessImageEvent;
import br.com.bix.images.config.security.JwtTokenProvider;
import br.com.bix.images.service.ImageService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessImageConsumer {

    private final ImageService imageService;
    private final JwtTokenProvider jwtTokenProvider;

    @KafkaListener(topics = PROCESS_IMAGE_TOPIC,
        containerFactory = CONTAINER_FACTORY_BEAN_NAME)
    public void consume(@NonNull ProcessImageEvent event, @NonNull Acknowledgment acknowledgment) {
        acknowledgment.acknowledge();
        try {
            log.info("Consuming event on topic PROCESS_IMAGE_TOPIC value: {}", event);
            validateTokenUser(event.getToken());
            imageService.process(event);
        } catch(RuntimeException ex) {
            log.error("Failed to process image by message: {}", ex.getMessage());
        }
    }

    private void validateTokenUser(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new BadCredentialsException("Invalid token");
        }
    }
}
