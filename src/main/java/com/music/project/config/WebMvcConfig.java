package com.music.project.config;

import com.music.project.interceptor.TokenRefreshInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenRefreshInterceptor tokenRefreshInterceptor;

    @Autowired
    public WebMvcConfig(TokenRefreshInterceptor tokenRefreshInterceptor) {
        this.tokenRefreshInterceptor = tokenRefreshInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenRefreshInterceptor).addPathPatterns("/client/spotify/**").addPathPatterns("https://accounts.spotify.com/**");
    }
}
