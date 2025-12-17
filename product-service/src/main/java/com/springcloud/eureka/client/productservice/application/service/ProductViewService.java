package com.springcloud.eureka.client.productservice.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class ProductViewService {

    private static final String VIEW_RANKING_KEY = "product:view_ranking";

    private final StringRedisTemplate stringRedisTemplate;

    public ProductViewService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        log.info("=== ProductViewService 생성! ===");
        testRedisConnection();
    }

    private void testRedisConnection() {
        try {
            log.info("=== Redis 연결 테스트 시작! ===");
            String pong = stringRedisTemplate.getConnectionFactory()
                    .getConnection().ping();
            log.info("=== PING 결과: {} ===", pong);

            Set<String> keys = stringRedisTemplate.keys("*");
            log.info("=== 전체 키: {} ===", keys);
            log.info("=== Redis 연결 테스트 성공! ===");

        } catch (Exception e) {
            log.error("=== Redis 연결 테스트 실패! ===", e);
        }
    }

    public void incrementViewCount(UUID productId) {
        try {
            log.info("=== 조회수 증가 시작: {} ===", productId);

            // ZINCRBY: 없으면 0에서 시작, 있으면 기존 값에 +1
            Double result = stringRedisTemplate.opsForZSet()
                    .incrementScore(VIEW_RANKING_KEY, productId.toString(), 1.0);

            log.info("=== ZINCRBY 결과: {} ===", result);

        } catch (Exception e) {
            log.error("=== 조회수 증가 실패: {} ===", productId, e);
        }
    }

    public Set<String> getTopProductIds(int topCount) {
        Set<String> result = stringRedisTemplate.opsForZSet()
                .reverseRange(VIEW_RANKING_KEY, 0, topCount - 1);
        return result != null ? result : Set.of();
    }

    public Double getViewCount(UUID productId) {
        return stringRedisTemplate.opsForZSet()
                .score(VIEW_RANKING_KEY, productId.toString());
    }
}
