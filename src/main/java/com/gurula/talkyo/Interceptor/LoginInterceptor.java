package com.gurula.talkyo.Interceptor;

import com.gurula.talkyo.jwt.JwtTool;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberService;
import com.gurula.talkyo.properties.ConfigProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpCookie;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Configuration
public class LoginInterceptor implements HandlerInterceptor{
    Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    private final ConfigProperties configProperties;
    private final JwtTool jwtTool;
    private final MemberService memberService;

    public LoginInterceptor(ConfigProperties configProperties, JwtTool jwtTool, MemberService memberService) {
        this.configProperties = configProperties;
        this.jwtTool = jwtTool;
        this.memberService = memberService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
//        logger.info("Request URL: " + requestURI + ", Method: " + method);

        String internalHeader = request.getHeader("Internal-Request");
        if ("true".equals(internalHeader)) {
            return true;
        }

        // 從 cookie 中取的 token
        String token = Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> "JWT_TOKEN".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst())
                .orElse(null);
        System.out.println("token = " + token);

        // 取出 token 內的 memberId
        final String memberId = jwtTool.parseToken(token);

        return memberService.findById(memberId)
                .map(member -> true)
                .orElseGet(() -> {
                    logger.warn("未登入索取的資源是：{}", request.getRequestURI());
                    try {
                        response.sendRedirect(configProperties.getGlobalDomain() + "login.html");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return false;
                });
    }

}
