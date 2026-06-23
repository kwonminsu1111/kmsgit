package com.ssafy.enjoytrip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ssafy.enjoytrip.interceptor.JwtInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns(
                        "/plans/**",
                        "/boards/**",
                        "/users/**",
                        "/main/**",
                        "/users/me",
                        "/users/logout",
                        "/users/delete"
                        )
                .excludePathPatterns(
                        "/users/login",
                        "/users/logout",
                        "/users/re-issue",
                        "/users/register",
                        "/swagger-ui/**",
                        "/v3/api-docs/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Refresh-Token", "Set-Cookie")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
