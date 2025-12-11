package com.javaauction.chatservice.infrastructure.repository;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterRepository {

    private final Map<UUID, Map<String, SseEmitter>> emitterMap = new ConcurrentHashMap<>();

    public SseEmitter save(UUID chatroomId, String userId, SseEmitter emitter) {
        emitterMap
                .computeIfAbsent(chatroomId, k -> new ConcurrentHashMap<>())
                .put(userId, emitter);
        return emitter;
    }

    public void delete(UUID chatroomId, String userId) {
        Map<String, SseEmitter> map = emitterMap.get(chatroomId);
        if (map != null) {
            map.remove(userId);
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
                map.remove(userId);
                emitter.completeWithError(e);
            }
        });
    }
}
