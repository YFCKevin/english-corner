package com.gurula.talkyo.webSocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry endpointRegistry) {
        // 註冊一個給 Client 連接到 WebSocket Server 的節點
        endpointRegistry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // 設置 CORS 允許的來源
                .withSockJS();                  // 啟用 SockJS 支援
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry brokerRegister) {

        // 負責將訊息推送給訂閱者
        brokerRegister.enableSimpleBroker("/init", "/chatroom") // "/chatroom/private", "/chatroom/temporary", "/chatroom/group"
                .setTaskScheduler(heartBeatScheduler());

        // 負責將訊息轉給 Controller 處理
        brokerRegister.setApplicationDestinationPrefixes("/chat");
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        return new ThreadPoolTaskScheduler();
    }

}
