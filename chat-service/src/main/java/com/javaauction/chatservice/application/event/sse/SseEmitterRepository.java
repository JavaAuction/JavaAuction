package com.javaauction.chatservice.application.event.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseEmitterRepository {

    private final Map<UUID, Map<String, SseEmitter>> emitterMap = new ConcurrentHashMap<>();

    public SseEmitter save(UUID chatroomId, String userId, SseEmitter emitter) {
        emitterMap
                .computeIfAbsent(chatroomId, k -> new ConcurrentHashMap<>())
                .put(userId, emitter);
        return emitter;
    }

    public void delete(UUID chatroomId, String userId, String role) {
        Map<String, SseEmitter> map = emitterMap.get(chatroomId);
        if (map != null) {
            map.remove(userId);
            log.info("[SSE] emitter removed: {} (role={})", userId, role);
        }
    }

    public void send(UUID chatroomId, String message) {
        Map<String, SseEmitter> map = emitterMap.get(chatroomId);
        if (map == null) return;

        map.forEach((userId, emitter) -> {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("chat-message")
                                .data(message)
                );
            } catch (IOException e) {
                log.warn("[SSE] 전송 실패, emitter 제거: {}", userId);
                map.remove(userId);
                emitter.completeWithError(e);
            }
        });
    }
}
