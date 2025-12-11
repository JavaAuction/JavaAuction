package com.example.review.infrastructure.kafka;

import com.example.review.infrastructure.event.UserGetResponseEvent;
import com.example.review.infrastructure.event.UserExistsResponseEvent;
import com.example.review.infrastructure.feign.dto.ResGetUserIntDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventService {

    private final UserEventProducer userEventProducer;
    private final UserEventConsumer userEventConsumer;

    public ResGetUserIntDto getUser(String userId) {
        try {
            String correlationId = userEventProducer.requestUserInfo(userId);
            CompletableFuture<Object> future = userEventConsumer.waitForResponse(correlationId);
            
            Object result = future.get(5, TimeUnit.SECONDS);
            if (result instanceof ResGetUserIntDto) {
                return (ResGetUserIntDto) result;
            }
            throw new RuntimeException("Invalid response type");
        } catch (Exception e) {
            log.error("Error getting user from Kafka: userId={}", userId, e);
            throw new RuntimeException("Failed to get user information", e);
        }
    }

    public boolean existsUser(String userId) {
        try {
            String correlationId = userEventProducer.requestUserExists(userId);
            CompletableFuture<Object> future = userEventConsumer.waitForResponse(correlationId);
            
            Object result = future.get(5, TimeUnit.SECONDS);
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            throw new RuntimeException("Invalid response type");
        } catch (Exception e) {
            log.error("Error checking user existence from Kafka: userId={}", userId, e);
            throw new RuntimeException("Failed to check user existence", e);
        }
    }
}


