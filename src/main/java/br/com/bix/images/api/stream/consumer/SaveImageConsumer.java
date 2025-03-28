package br.com.bix.images.api.stream.consumer;

import static br.com.bix.images.api.stream.consumer.SaveImageConsumerConfig.CONTAINER_FACTORY_BEAN_NAME;
import static br.com.bix.images.config.kafka.KafkaConfig.SAVE_IMAGE_TOPIC;

import br.com.bix.images.api.stream.model.SaveImageEvent;
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
public class SaveImageConsumer {

    private final ImageService imageService;
    private final JwtTokenProvider jwtTokenProvider;

    @KafkaListener(topics = SAVE_IMAGE_TOPIC,
        containerFactory = CONTAINER_FACTORY_BEAN_NAME)
    public void consume(@NonNull SaveImageEvent event, @NonNull Acknowledgment acknowledgment) {
        acknowledgment.acknowledge();
        try {
            log.info("Consuming event on topic SAVE_IMAGE_TOPIC value: {}", event);
            validateTokenUser(event.getToken());
            imageService.save(event);
        } catch(RuntimeException ex) {
            log.error("Failed to save image by message: {}", ex.getMessage());
        }
    }

    private void validateTokenUser(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new BadCredentialsException("Invalid token");
        }
    }
}
