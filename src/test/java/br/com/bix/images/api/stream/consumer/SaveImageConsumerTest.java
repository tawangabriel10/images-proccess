package br.com.bix.images.api.stream.consumer;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import br.com.bix.images.Fixture;
import br.com.bix.images.api.stream.model.SaveImageEvent;
import br.com.bix.images.config.security.JwtTokenProvider;
import br.com.bix.images.service.ImageService;
import br.com.bix.images.service.model.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

@ExtendWith(MockitoExtension.class)
class SaveImageConsumerTest {

    @InjectMocks private SaveImageConsumer saveImageConsumer;
    @Mock private ImageService imageService;
    @Mock private Acknowledgment acknowledgment;
    @Mock private JwtTokenProvider jwtTokenProvider;

    @Test
    void givenSaveImageEvent_whenCallConsume_thenSaveImage() {
        final SaveImageEvent event = Fixture.make(SaveImageEvent.builder().build());

        doNothing().when(acknowledgment).acknowledge();
        when(jwtTokenProvider.validateToken(any())).thenReturn(Boolean.TRUE);
        doNothing().when(imageService).save(any());

        saveImageConsumer.consume(event, acknowledgment);

        verify(acknowledgment).acknowledge();
        verify(jwtTokenProvider).validateToken(event.getToken());
        verify(imageService).save(event);
    }

    @Test
    void givenSaveImageEventWithTokenInvalid_whenCallConsume_thenThrowsBadCredentialsException() {
        final SaveImageEvent event = Fixture.make(SaveImageEvent.builder().build());

        doNothing().when(acknowledgment).acknowledge();
        when(jwtTokenProvider.validateToken(any())).thenReturn(Boolean.FALSE);

        saveImageConsumer.consume(event, acknowledgment);

        verify(acknowledgment).acknowledge();
        verify(jwtTokenProvider).validateToken(event.getToken());
        verifyNoInteractions(imageService);
    }
}
