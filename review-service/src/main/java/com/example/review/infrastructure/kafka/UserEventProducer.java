package com.example.review.infrastructure.kafka;

import com.example.review.infrastructure.event.UserGetRequestEvent;
import com.example.review.infrastructure.event.UserExistsRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public String requestUserInfo(String userId) {
        String correlationId = UUID.randomUUID().toString();
        UserGetRequestEvent event = new UserGetRequestEvent(userId, correlationId);
        
        log.info("Sending user get request event: userId={}, correlationId={}", userId, correlationId);
        kafkaTemplate.send("user.get.request", correlationId, event);
        
        return correlationId;
    }

    public String requestUserExists(String userId) {
        String correlationId = UUID.randomUUID().toString();
        UserExistsRequestEvent event = new UserExistsRequestEvent(userId, correlationId);
        
        log.info("Sending user exists request event: userId={}, correlationId={}", userId, correlationId);
        kafkaTemplate.send("user.exists.request", correlationId, event);
        
        return correlationId;
    }
}


