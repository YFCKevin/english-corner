package com.gurula.talkyo.webSocket;

import com.gurula.talkyo.exception.InvalidTokenException;
import com.gurula.talkyo.jwt.JwtTool;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WebSocketLoginInterceptor implements HandshakeInterceptor {

    private final JwtTool jwtTool;
    private final MemberService memberService;

    public WebSocketLoginInterceptor(JwtTool jwtTool, MemberService memberService) {
        this.jwtTool = jwtTool;
        this.memberService = memberService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        // 從 headers 中獲取 Cookie
        List<String> cookies = request.getHeaders().get(HttpHeaders.COOKIE);
        System.out.println("cookies = " + cookies);
        if (cookies != null) {
            // 找 JWT_TOKEN cookie
            Optional<String> tokenOpt = cookies.stream()
                    .flatMap(cookieHeader -> Arrays.stream(cookieHeader.split(";")))
                    .map(String::trim)
                    .filter(cookie -> cookie.startsWith("JWT_TOKEN="))
                    .map(cookie -> cookie.substring("JWT_TOKEN=".length()))
                    .findFirst();

            if (tokenOpt.isPresent()) {
                String token = tokenOpt.get();
                System.out.println("token = " + token);

                try {
                    final String memberId = jwtTool.parseToken(token);
                    final Member member = memberService.findById(memberId).get();
                    attributes.put("member", member);
                    System.out.println("memberId = " + memberId);
                    return true;
                } catch (InvalidTokenException t) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    response.getHeaders().add("WebSocket-Error", "Unauthorized");  // 可選，加入錯誤訊息
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
