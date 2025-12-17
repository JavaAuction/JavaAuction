package com.javaauction.gateway.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // 모든 origin 허용 (개발 환경)
        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        
        // 모든 HTTP 메서드 허용
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // 모든 헤더 허용
        corsConfiguration.setAllowedHeaders(List.of("*"));
        
        // 인증 정보 허용 (와일드카드 origin 사용 시 false)
        corsConfiguration.setAllowCredentials(false);
        
        // 모든 응답 헤더 노출
        corsConfiguration.setExposedHeaders(List.of("*"));
        
        // Preflight 요청 캐시 시간 (초)
        corsConfiguration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsWebFilter(source);
    }
}

