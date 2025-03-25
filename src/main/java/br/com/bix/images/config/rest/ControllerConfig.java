package br.com.bix.images.config.rest;

import br.com.bix.images.service.model.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ControllerConfig {

    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error businessException(final BusinessException ex) {
        log.error(ex.getMessage(), ex);

        return Error.builder()
            .message(ex.getMessage())
            .build();
    }

    @ExceptionHandler({BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Error businessException(final BadCredentialsException ex) {
        log.error(ex.getMessage(), ex);

        return Error.builder()
            .message(ex.getMessage())
            .build();
    }
}
