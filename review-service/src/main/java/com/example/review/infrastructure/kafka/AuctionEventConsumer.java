package com.example.review.infrastructure.kafka;

import com.example.review.infrastructure.event.AuctionValidationResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionEventConsumer {

    private final Map<String, CompletableFuture<Object>> pendingRequests = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "auction.validation.response", groupId = "review-service-group")
    public void consumeAuctionValidationResponse(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            log.info("Received auction validation response: {}", payload);
            
            // Map에서 직접 값을 추출하여 변환 (Jackson의 boolean 필드 변환 문제 방지)
            String correlationId = (String) payload.get("correlationId");
            Object isValidObj = payload.get("isValid");
            boolean isValid = false;
            if (isValidObj instanceof Boolean) {
                isValid = (Boolean) isValidObj;
            } else if (isValidObj instanceof String) {
                isValid = Boolean.parseBoolean((String) isValidObj);
            }
            String sellerId = (String) payload.get("sellerId");
            String buyerId = (String) payload.get("buyerId");
            
            AuctionValidationResponseEvent response = AuctionValidationResponseEvent.builder()
                    .correlationId(correlationId)
                    .isValid(isValid)
                    .sellerId(sellerId)
                    .buyerId(buyerId)
                    .build();
            
            log.info("Processing auction validation response: correlationId={}, isValid={}, sellerId={}, buyerId={}", 
                    correlationId, response.isValid(), response.getSellerId(), response.getBuyerId());
            
            CompletableFuture<Object> future = pendingRequests.remove(correlationId);
            if (future != null) {
                future.complete(response);
                log.info("Completed future for correlationId: {}", correlationId);
            } else {
                log.warn("No pending request found for correlationId: {}", correlationId);
            }
        } catch (Exception e) {
            log.error("Error processing auction validation response", e);
        } finally {
            acknowledgment.acknowledge();
        }
    }

    public CompletableFuture<Object> waitForResponse(String correlationId) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);
        log.info("Waiting for response with correlationId: {}", correlationId);
        return future;
    }
}

