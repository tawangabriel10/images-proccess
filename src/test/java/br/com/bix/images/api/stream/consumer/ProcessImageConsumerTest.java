package br.com.bix.images.api.stream.consumer;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import br.com.bix.images.Fixture;
import br.com.bix.images.api.stream.model.ProcessImageEvent;
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
class ProcessImageConsumerTest {

    @InjectMocks private ProcessImageConsumer consumer;
    @Mock private ImageService imageService;
    @Mock private Acknowledgment acknowledgment;
    @Mock private JwtTokenProvider jwtTokenProvider;

    @Test
    void givenProcessImageEvent_whenCallConsume_thenProcessImage() {
        final ProcessImageEvent event = Fixture.make(ProcessImageEvent.builder().build());

        doNothing().when(acknowledgment).acknowledge();
        when(jwtTokenProvider.validateToken(any())).thenReturn(Boolean.TRUE);
        doNothing().when(imageService).process(any());

        consumer.consume(event, acknowledgment);

        verify(acknowledgment).acknowledge();
        verify(jwtTokenProvider).validateToken(event.getToken());
        verify(imageService).process(event);
    }

    @Test
    void givenProcessImageEventWithTokenInvalid_whenCallConsume_thenThrowsBadCredentialsException() {
        final ProcessImageEvent event = Fixture.make(ProcessImageEvent.builder().build());

        doNothing().when(acknowledgment).acknowledge();
        when(jwtTokenProvider.validateToken(any())).thenReturn(Boolean.FALSE);

        consumer.consume(event, acknowledgment);

        verify(acknowledgment).acknowledge();
        verify(jwtTokenProvider).validateToken(event.getToken());
        verifyNoInteractions(imageService);
    }
}
