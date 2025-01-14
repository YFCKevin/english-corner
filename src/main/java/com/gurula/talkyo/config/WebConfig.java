package com.gurula.talkyo.config;

import com.gurula.talkyo.interceptor.LoginInterceptor;
import com.gurula.talkyo.properties.ConfigProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final ConfigProperties configProperties;
    private final LoginInterceptor loginInterceptor;
    public WebConfig(ConfigProperties configProperties, LoginInterceptor loginInterceptor) {
        this.configProperties = configProperties;
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/talkyo/image/**").addResourceLocations("file:"+ configProperties.getPicSavePath());
        registry.addResourceHandler("/talkyo/audio/**").addResourceLocations("file:"+ configProperties.getAudioSavePath());
        registry.addResourceHandler("/talkyo/**").addResourceLocations("classpath:/static/");
//        super.addResourceHandlers(registry);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login.html", "/index.html")
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/webfonts/**", "/fonts/**", "/file/**", "/image/**", "/audio/**");
    }


    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
        configurer.setUseTrailingSlashMatch(true);
    }
}
