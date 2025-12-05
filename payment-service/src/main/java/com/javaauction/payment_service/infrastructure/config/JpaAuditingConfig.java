package com.javaauction.payment_service.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static com.javaauction.payment_service.presentation.constant.HttpHeaderNames.USERNAME;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attrs == null) return Optional.of("system");

            HttpServletRequest request = attrs.getRequest();
            String username = request.getHeader(USERNAME);

            if (username == null || username.isBlank()) return Optional.of("system");

            return Optional.of(username);
        };
    }
}
