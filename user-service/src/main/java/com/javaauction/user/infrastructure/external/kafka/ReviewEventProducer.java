package com.javaauction.user.infrastructure.external.kafka;

import com.javaauction.user.infrastructure.external.event.RatingGetRequestEvent;
import com.javaauction.user.infrastructure.external.event.ReviewDeleteRequestEvent;
import com.javaauction.user.infrastructure.external.event.ReviewGetRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public String requestReviewInfo(String userId) {
        String correlationId = UUID.randomUUID().toString();
        ReviewGetRequestEvent event = new ReviewGetRequestEvent(userId, correlationId);
        
        log.info("Sending review get request event: userId={}, correlationId={}", userId, correlationId);
        kafkaTemplate.send("review.get.request", correlationId, event);
        
        return correlationId;
    }

    public String requestRating(String userId) {
        String correlationId = UUID.randomUUID().toString();
        RatingGetRequestEvent event = new RatingGetRequestEvent(userId, correlationId);
        
        log.info("Sending rating get request event: userId={}, correlationId={}", userId, correlationId);
        kafkaTemplate.send("review.rating.get.request", correlationId, event);
        
        return correlationId;
    }

    public void requestReviewDelete(String userId) {
        ReviewDeleteRequestEvent event = new ReviewDeleteRequestEvent(userId);
        
        log.info("Sending review delete request event: userId={}", userId);
        kafkaTemplate.send("review.delete.request", userId, event);
    }
}


