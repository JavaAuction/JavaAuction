package com.javaauction.gateway.infrastructure.filter;

import com.javaauction.gateway.infrastructure.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    
    // 인증이 필요 없는 경로 목록
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/v1/auth/login",
            "/v1/auth/signup",
            "/internal/",
            "/actuator/"
    );

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 인증이 필요 없는 경로는 통과
        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        // Authorization 헤더에서 토큰 추출
        String token = extractToken(request);
        
        if (!StringUtils.hasText(token)) {
            log.warn("JWT 토큰이 없습니다. 경로: {}", path);
            return onError(exchange, "JWT 토큰이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        // 토큰 유효성 검증
        if (!jwtUtil.validateToken(token)) {
            log.warn("유효하지 않은 JWT 토큰입니다. 경로: {}", path);
            return onError(exchange, "유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED);
        }

        // 토큰 만료 확인
        if (jwtUtil.isTokenExpired(token)) {
            log.warn("만료된 JWT 토큰입니다. 경로: {}", path);
            return onError(exchange, "만료된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED);
        }

        // 토큰에서 사용자 정보 추출하여 헤더에 추가
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            // 사용자 정보를 헤더에 추가하여 하위 서비스로 전달
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Username", username)
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            log.error("JWT 토큰 파싱 중 오류 발생: {}", e.getMessage());
            return onError(exchange, "JWT 토큰 처리 중 오류가 발생했습니다.", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 인증이 필요 없는 경로인지 확인
     */
    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream()
                .anyMatch(excludedPath -> path.startsWith(excludedPath));
    }

    /**
     * Authorization 헤더에서 JWT 토큰 추출
     */
    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 에러 응답 처리
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        
        String body = String.format("{\"error\":\"%s\"}", message);
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes()))
        );
    }

    @Override
    public int getOrder() {
        return -100; // 높은 우선순위로 필터 실행
    }
}

