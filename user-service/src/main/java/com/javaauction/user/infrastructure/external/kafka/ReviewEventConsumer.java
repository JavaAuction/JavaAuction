package com.javaauction.user.infrastructure.external.kafka;

import com.javaauction.user.infrastructure.external.dto.GetReviewIntDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
