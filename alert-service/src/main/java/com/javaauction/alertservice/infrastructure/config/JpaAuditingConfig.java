package com.javaauction.alertservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            // 현재 요청에서 사용자 헤더 가져오기
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                return Optional.of("system"); // 요청 없으면 system 처리
            }

            HttpServletRequest request = attrs.getRequest();
            String username = request.getHeader("X-User-Username");

            return Optional.ofNullable(username).or(() -> Optional.of("system"));
        };
    }
}