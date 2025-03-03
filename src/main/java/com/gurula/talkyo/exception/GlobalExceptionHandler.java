package com.gurula.talkyo.exception;

import com.gurula.talkyo.properties.ConfigProperties;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final ConfigProperties configProperties;

    public GlobalExceptionHandler(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @ExceptionHandler(InvalidTokenException.class)
    public RedirectView handleInvalidTokenException(InvalidTokenException ex) {
        return new RedirectView(configProperties.getGlobalDomain() + "sign-in.html");
    }
}
