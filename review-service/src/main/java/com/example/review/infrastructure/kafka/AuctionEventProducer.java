package com.example.review.infrastructure.kafka;

import com.example.review.infrastructure.event.AuctionValidationRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public String requestAuctionValidation(UUID auctionId, String userId) {
        String correlationId = UUID.randomUUID().toString();
        AuctionValidationRequestEvent event = AuctionValidationRequestEvent.builder()
                .auctionId(auctionId)
                .userId(userId)
                .correlationId(correlationId)
                .build();
        
        log.info("Sending auction validation request event: auctionId={}, userId={}, correlationId={}", 
                auctionId, userId, correlationId);
        kafkaTemplate.send("auction.validation.request", correlationId, event);
        
        return correlationId;
    }
}

