package com.javaauction.user.infrastructure.external.kafka;

import com.javaauction.user.infrastructure.external.dto.ResInternalBidsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionEventService {

    private final AuctionEventProducer auctionEventProducer;
    private final AuctionEventConsumer auctionEventConsumer;

    public ResInternalBidsDto getBidsInternal(String userId) {
        try {
            String correlationId = auctionEventProducer.requestBids(userId);
            CompletableFuture<Object> future = auctionEventConsumer.waitForResponse(correlationId);
            
            Object result = future.get(5, TimeUnit.SECONDS);
            if (result instanceof ResInternalBidsDto) {
                return (ResInternalBidsDto) result;
            }
            throw new RuntimeException("Invalid response type");
        } catch (Exception e) {
            log.error("Error getting bids from Kafka: userId={}", userId, e);
            throw new RuntimeException("Failed to get bid information", e);
        }
    }
}

