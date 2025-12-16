package com.javaauction.user.infrastructure.external.kafka;

import com.javaauction.user.application.service.ReviewCacheService;
import com.javaauction.user.infrastructure.external.dto.GetReviewIntDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewEventConsumer {

    private final Map<String, CompletableFuture<Object>> pendingRequests = new ConcurrentHashMap<>();
    private final CacheManager cacheManager;

    @KafkaListener(topics = "review.get.response", groupId = "user-service-group")
    public void consumeReviewGetResponse(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String correlationId = (String) payload.get("correlationId");
            List<Map<String, Object>> reviewsList = (List<Map<String, Object>>) payload.get("reviews");
            
            log.info("Received review get response: correlationId={}", correlationId);
            
            List<GetReviewIntDto> reviews = reviewsList.stream()
                    .map(reviewMap -> {
                        Object ratingObj = reviewMap.get("rating");
                        double rating = ratingObj instanceof Number ? ((Number) ratingObj).doubleValue() : 0.0;
                        
                        return GetReviewIntDto.builder()
                                .writer((String) reviewMap.get("writer"))
                                .target((String) reviewMap.get("target"))
                                .rating(rating)
                                .content((String) reviewMap.get("content"))
                                .build();
                    })
                    .collect(Collectors.toList());
            
            CompletableFuture<Object> future = pendingRequests.remove(correlationId);
            if (future != null) {
                future.complete(reviews);
            }
        } catch (Exception e) {
            log.error("Error processing review get response", e);
        }
        
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "review.rating.get.response", groupId = "user-service-group")
    public void consumeRatingGetResponse(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String correlationId = (String) payload.get("correlationId");
            Object ratingObj = payload.get("rating");
            double rating = ratingObj instanceof Number ? ((Number) ratingObj).doubleValue() : 0.0;
            
            log.info("Received rating get response: correlationId={}, rating={}", correlationId, rating);
            
            CompletableFuture<Object> future = pendingRequests.remove(correlationId);
            if (future != null) {
                future.complete(rating);
            }
        } catch (Exception e) {
            log.error("Error processing rating get response", e);
        }
        
        acknowledgment.acknowledge();
    }

    public CompletableFuture<Object> waitForResponse(String correlationId) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);
        return future;
    }

    /**
     * 리뷰 변경 이벤트 수신 - 리뷰 캐시 무효화
     * review-service에서 리뷰 생성/수정/삭제 시 발행하는 이벤트를 수신
     */
    @KafkaListener(topics = "review.changed", groupId = "user-service-group")
    public void consumeReviewChanged(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String userId = (String) payload.get("userId");
            String targetUserId = (String) payload.get("targetUserId");
            
            log.info("리뷰 변경 이벤트 수신: userId={}, targetUserId={}, 캐시 무효화", userId, targetUserId);
            
            // 리뷰를 작성한 사용자와 리뷰를 받은 사용자 모두의 캐시 무효화
            if (userId != null) {
                evictReviewCache(userId);
            }
            if (targetUserId != null) {
                evictReviewCache(targetUserId);
            }
            
        } catch (Exception e) {
            log.error("리뷰 변경 이벤트 처리 중 오류", e);
        }
        
        acknowledgment.acknowledge();
    }

    /**
     * 특정 사용자의 리뷰 캐시 무효화 (reviews + rating 모두)
     */
    private void evictReviewCache(String userId) {
        try {
            var cache = cacheManager.getCache("review");
            if (cache != null) {
                cache.evict("reviews_" + userId);  // 리뷰 목록 캐시 무효화
                cache.evict("rating_" + userId);    // 평점 캐시 무효화
                log.info("리뷰 캐시 무효화 완료: userId={} (reviews + rating)", userId);
            }
        } catch (Exception e) {
            log.error("리뷰 캐시 무효화 실패: userId={}", userId, e);
        }
    }
}
