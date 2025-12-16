package com.javaauction.chatservice.application.event.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaauction.chatservice.domain.entity.Chatroom;
import com.javaauction.chatservice.domain.entity.Chatting;
import com.javaauction.chatservice.infrastructure.repository.ChatroomJpaRepository;
import com.javaauction.chatservice.presentation.advice.ChatErrorCode;
import com.javaauction.global.presentation.exception.BussinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseEmitterService {

    private final SseEmitterRepository emitterRepository;
    private final ObjectMapper objectMapper;

    private static final long TIMEOUT = 1000L * 60 * 60;

    public SseEmitter subscribe(UUID chatroomId, String userId) {

        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterRepository.save(chatroomId, userId, emitter);

        emitter.onCompletion(() -> emitterRepository.delete(chatroomId, userId));
        emitter.onTimeout(() -> emitterRepository.delete(chatroomId, userId));
        emitter.onError(e -> emitterRepository.delete(chatroomId, userId));

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (Exception ignored) {}

        return emitter;
    }

    public void sendChatMessage(UUID chatroomId, Chatting chatData) {
        try {
            Map<String, Object> payload = Map.of(
                    "senderId", chatData.getSenderId(),
                    "content", chatData.getContent(),
                    "createdAt", chatData.getCreatedAt()
            );

            emitterRepository.send(chatroomId,
                    objectMapper.writeValueAsString(payload));

        } catch (Exception e) {
            log.error("[SSE] send error", e);
        }
    }
}


