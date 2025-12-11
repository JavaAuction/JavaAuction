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

    private final Map<UUID, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public SseEmitter save(UUID chatroomId, SseEmitter emitter) {
        emitterMap.put(chatroomId, emitter);
        return emitter;
    }

    public void delete(UUID chatroomId) {
        emitterMap.remove(chatroomId);
    }

    public void send(UUID chatroomId, String message) {
        SseEmitter emitter = emitterMap.get(chatroomId);
        if (emitter == null) return;

        try {
            emitter.send(
                    SseEmitter.event()
                            .name("chat-message")
                            .data(message)
            );
        } catch (IOException e) {
            emitterMap.remove(chatroomId);
            emitter.completeWithError(e);
        }
    }
}
