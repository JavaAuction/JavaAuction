package com.javaauction.user.infrastructure.external.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaauction.user.application.service.UserServiceV1;
import com.javaauction.user.presentation.dto.ResGetUserIntDto;
import com.javaauction.user.infrastructure.external.event.UserGetResponseEvent;
import com.javaauction.user.infrastructure.external.event.UserExistsResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceV1 userServiceV1;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user.get.request", groupId = "user-service-group")
    public void consumeUserGetRequest(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String userId = (String) payload.get("userId");
            String correlationId = (String) payload.get("correlationId");
            
            log.info("Received user get request: userId={}, correlationId={}", userId, correlationId);
            
            ResGetUserIntDto user = userServiceV1.getUserInternal(userId);
            
            ResGetUserIntDto responseDto =
                    ResGetUserIntDto.builder()
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .address(user.getAddress())
                            .slackId(user.getSlackId())
                            .role(user.getRole())
                            .build();
            
            UserGetResponseEvent responseEvent = new UserGetResponseEvent(
                    correlationId,
                    responseDto
            );
            
            log.info("Sending user get response: correlationId={}", correlationId);
            kafkaTemplate.send("user.get.response", correlationId, responseEvent);
        } catch (Exception e) {
            log.error("Error processing user get request", e);
        }
        
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "user.exists.request", groupId = "user-service-group")
    public void consumeUserExistsRequest(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String userId = (String) payload.get("userId");
            String correlationId = (String) payload.get("correlationId");
            
            log.info("Received user exists request: userId={}, correlationId={}", userId, correlationId);
            
            boolean exists = userServiceV1.existsUser(userId);
            
            UserExistsResponseEvent responseEvent = new UserExistsResponseEvent(
                    correlationId,
                    exists
            );
            
            log.info("Sending user exists response: correlationId={}, exists={}", correlationId, exists);
            kafkaTemplate.send("user.exists.response", correlationId, responseEvent);
        } catch (Exception e) {
            log.error("Error processing user exists request", e);
        }
        
        acknowledgment.acknowledge();
    }
}

