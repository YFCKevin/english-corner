package com.gurula.talkyo.chatroom.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoRHandlerConfig {
    @Bean
    public MessageTypeHandler messageTypeHandler (){
        return new AudioProcessor(new ImageProcessor(null));
    }
}
