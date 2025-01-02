package com.musinsa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 CORS 적용
                .allowedOrigins("*") // 모든 Origin 허용 (프로덕션 환경에서는 수정 필요)
                .allowedMethods("*") // 모든 HTTP Method 허용
                .allowedHeaders("*"); // 모든 HTTP Header 허용
    }
}