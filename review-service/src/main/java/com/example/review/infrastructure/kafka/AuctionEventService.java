package com.example.review.infrastructure.kafka;

import com.example.review.infrastructure.event.AuctionValidationResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionEventService {

    private final AuctionEventProducer auctionEventProducer;
    private final AuctionEventConsumer auctionEventConsumer;

    public AuctionValidationResponseEvent validateAuction(UUID auctionId, String userId) {
        try {
            String correlationId = auctionEventProducer.requestAuctionValidation(auctionId, userId);
            CompletableFuture<Object> future = auctionEventConsumer.waitForResponse(correlationId);
            
            Object result = future.get(5, TimeUnit.SECONDS);
            if (result instanceof AuctionValidationResponseEvent) {
                return (AuctionValidationResponseEvent) result;
            }
            throw new RuntimeException("Invalid response type");
        } catch (Exception e) {
            log.error("Error validating auction from Kafka: auctionId={}, userId={}", auctionId, userId, e);
            throw new RuntimeException("Failed to validate auction", e);
        }
    }
}

