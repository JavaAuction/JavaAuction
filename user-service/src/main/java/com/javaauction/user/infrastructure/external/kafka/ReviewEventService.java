package com.javaauction.user.infrastructure.external.kafka;

import com.javaauction.user.infrastructure.external.dto.GetReviewIntDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewEventService {

    private final ReviewEventProducer reviewEventProducer;
    private final ReviewEventConsumer reviewEventConsumer;

    public List<GetReviewIntDto> getReviewByUser(String userId) {
        try {
            String correlationId = reviewEventProducer.requestReviewInfo(userId);
            CompletableFuture<Object> future = reviewEventConsumer.waitForResponse(correlationId);
            
            Object result = future.get(5, TimeUnit.SECONDS);
            if (result instanceof List) {
                @SuppressWarnings("unchecked")
                List<GetReviewIntDto> reviews = (List<GetReviewIntDto>) result;
                return reviews;
            }
            throw new RuntimeException("Invalid response type");
        } catch (Exception e) {
            log.error("Error getting reviews from Kafka: userId={}", userId, e);
            throw new RuntimeException("Failed to get review information", e);
        }
    }

    public double getUserRating(String userId) {
        try {
            String correlationId = reviewEventProducer.requestRating(userId);
            CompletableFuture<Object> future = reviewEventConsumer.waitForResponse(correlationId);
            
            Object result = future.get(5, TimeUnit.SECONDS);
            if (result instanceof Double) {
                return (Double) result;
            } else if (result instanceof Number) {
                return ((Number) result).doubleValue();
            }
            throw new RuntimeException("Invalid response type");
        } catch (Exception e) {
            log.error("Error getting rating from Kafka: userId={}", userId, e);
            throw new RuntimeException("Failed to get rating information", e);
        }
    }

    public void deleteAllByUserId(String userId) {
        reviewEventProducer.requestReviewDelete(userId);
    }
}
