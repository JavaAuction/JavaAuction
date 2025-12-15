package com.example.review.infrastructure.kafka;

import com.example.review.infrastructure.event.AuctionValidationResponseEvent;
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

    @KafkaListener(topics = "auction.validation.response", groupId = "review-service-group")
    public void consumeAuctionValidationResponse(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String correlationId = (String) payload.get("correlationId");
            Boolean isValid = (Boolean) payload.get("isValid");
            String sellerId = (String) payload.get("sellerId");
            String buyerId = (String) payload.get("buyerId");
            
            log.info("Received auction validation response: correlationId={}, isValid={}", correlationId, isValid);
            
            AuctionValidationResponseEvent response = AuctionValidationResponseEvent.builder()
                    .correlationId(correlationId)
                    .isValid(isValid != null ? isValid : false)
                    .sellerId(sellerId)
                    .buyerId(buyerId)
                    .build();
            
            CompletableFuture<Object> future = pendingRequests.remove(correlationId);
            if (future != null) {
                future.complete(response);
            }
        } catch (Exception e) {
            log.error("Error processing auction validation response", e);
        }
        
        acknowledgment.acknowledge();
    }

    public CompletableFuture<Object> waitForResponse(String correlationId) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);
        return future;
    }
}

