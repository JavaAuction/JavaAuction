package com.javaauction.user.infrastructure.external.kafka;

import com.javaauction.user.infrastructure.external.event.BidGetRequestEvent;
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

    public String requestBids(String userId) {
        String correlationId = UUID.randomUUID().toString();
        BidGetRequestEvent event = new BidGetRequestEvent(userId, correlationId);
        
        log.info("Sending bid get request event: userId={}, correlationId={}", userId, correlationId);
        kafkaTemplate.send("bid.get.request", correlationId, event);
        
        return correlationId;
    }
}

