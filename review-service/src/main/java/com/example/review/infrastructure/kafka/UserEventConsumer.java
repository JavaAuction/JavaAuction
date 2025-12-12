package com.example.review.infrastructure.kafka;

import com.example.review.infrastructure.feign.dto.ResGetUserIntDto;
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
public class UserEventConsumer {

    private final Map<String, CompletableFuture<Object>> pendingRequests = new ConcurrentHashMap<>();

    @KafkaListener(topics = "user.get.response", groupId = "review-service-group")
    public void consumeUserGetResponse(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String correlationId = (String) payload.get("correlationId");
            Map<String, Object> userMap = (Map<String, Object>) payload.get("user");
            
            log.info("Received user get response: correlationId={}", correlationId);
            
            ResGetUserIntDto user = ResGetUserIntDto.builder()
                    .username((String) userMap.get("username"))
                    .email((String) userMap.get("email"))
                    .address((String) userMap.get("address"))
                    .slackId((String) userMap.get("slackId"))
                    .role((String) userMap.get("role"))
                    .build();
            
            CompletableFuture<Object> future = pendingRequests.remove(correlationId);
            if (future != null) {
                future.complete(user);
            }
        } catch (Exception e) {
            log.error("Error processing user get response", e);
        }
        
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "user.exists.response", groupId = "review-service-group")
    public void consumeUserExistsResponse(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String correlationId = (String) payload.get("correlationId");
            Boolean exists = (Boolean) payload.get("exists");
            
            log.info("Received user exists response: correlationId={}, exists={}", correlationId, exists);
            
            CompletableFuture<Object> future = pendingRequests.remove(correlationId);
            if (future != null) {
                future.complete(exists);
            }
        } catch (Exception e) {
            log.error("Error processing user exists response", e);
        }
        
        acknowledgment.acknowledge();
    }

    public CompletableFuture<Object> waitForResponse(String correlationId) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);
        return future;
    }
}


