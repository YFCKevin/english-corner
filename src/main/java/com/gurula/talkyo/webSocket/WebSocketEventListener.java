package com.gurula.talkyo.webSocket;

import com.gurula.talkyo.interceptor.LoginInterceptor;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Map;
import java.util.Optional;

/**
 * WebSocket連線事件監聽器
 */
@Component
public class WebSocketEventListener {

    /**
     * STOMP 訊息發送器
     */
    private final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    private final MemberService memberService;

    public WebSocketEventListener(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 連線時的處理
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        System.out.println("收到一個新的 WebSocket 連線，Session ID: " + sessionId);
    }

    /**
     * 離線時的處理
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println("event = " + event);
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // 從WebSocket Session中取得使用者名稱
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            System.out.println("使用者" + username + "已離線");
        }
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        String destination = event.getMessage().getHeaders().get("simpDestination", String.class);
        if (destination != null && destination.startsWith("/chatroom")) {
            final String chatroomId = destination.split("/")[2];

            // 從 WebSocket session attributes 取得 Member
            SimpMessageHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
            if (headerAccessor != null) {
                Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
                if (sessionAttributes != null) {
                    Member member = (Member) sessionAttributes.get("member");

                    if (member != null) {
                        logger.info("使用者 [{},{}] 進入聊天室ID: {}", member.getName(), member.getId(), chatroomId);
                        return;
                    }
                }
            }

            logger.warn("無法取得 Member，拒絕進入聊天室ID {}", chatroomId);
        }
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        final String sessionId = headerAccessor.getSessionId();
        System.out.printf("Session ID: %s 取消訂閱: \n", sessionId);
    }

}
