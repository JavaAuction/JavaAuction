package com.javaauction.chatservice.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaauction.chatservice.domain.entity.Chatroom;
import com.javaauction.chatservice.domain.entity.Chatting;
import com.javaauction.chatservice.infrastructure.repository.ChatroomJpaRepository;
import com.javaauction.chatservice.infrastructure.repository.SseEmitterRepository;
import com.javaauction.chatservice.presentation.advice.ChatErrorCode;
import com.javaauction.global.presentation.exception.BussinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterServiceV1 {

    private final SseEmitterRepository emitterRepository;
    private final ChatroomJpaRepository chatroomRepository;
    private final ObjectMapper objectMapper;

    private static final long TIMEOUT = 1000L * 60 * 60; // 1시간

    public SseEmitter subscribe(UUID chatroomId, String userId, String role) {

        // 권한 체크
        validateAccess(chatroomId, userId, role);

        SseEmitter emitter = new SseEmitter(TIMEOUT);

        emitterRepository.save(chatroomId, userId, emitter);

        emitter.onCompletion(() -> {
            log.info("[SSE] onCompletion: {}", userId);
            emitterRepository.delete(chatroomId, userId, role);
        });

        emitter.onTimeout(() -> {
            log.info("[SSE] onTimeout: {}", userId);
            emitterRepository.delete(chatroomId, userId, role);
        });

        emitter.onError(e -> {
            log.error("[SSE] onError: {}", userId);
            emitterRepository.delete(chatroomId, userId, role);
        });

        // 초기 연결 이벤트
        try {
            emitter.send(
                    SseEmitter.event()
                            .name("connect")
                            .data("connected")
            );
        } catch (Exception ignored) {}

        return emitter;
    }


    private void validateAccess(UUID chatroomId, String userId, String role) {

        // ADMIN은 모든 채팅방 접근 가능
        if ("ADMIN".equals(role)) return;

        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new BussinessException(ChatErrorCode.CHAT_CHATROOM_NOT_FOUND));

        boolean isMember =
                chatroom.getChatroomHost().equals(userId) ||
                        chatroom.getChatroomGuest().equals(userId);

        if (!isMember) {
            throw new BussinessException(ChatErrorCode.CHATROOM_ACCESS_DENIED);
        }
    }


    public void sendChatMessage(UUID chatroomId, Chatting chatData) {

        String json;
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("senderId", chatData.getSenderId());
            payload.put("content", chatData.getContent());
            payload.put("createdAt", chatData.getCreatedAt());

            json = objectMapper.writeValueAsString(payload);

        } catch (Exception e) {
            log.error("[SSE] JSON 직렬화 실패", e);
            return;
        }

        emitterRepository.send(chatroomId, json);
    }
}
