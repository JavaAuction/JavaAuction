package com.javaauction.chatservice.application.service;

import com.javaauction.chatservice.domain.entity.Chatting;
import com.javaauction.chatservice.infrastructure.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SseEmitterServiceV1 {

    private static final MediaType UTF8_PLAIN =
            new MediaType("text", "plain", StandardCharsets.UTF_8);

    private final SseEmitterRepository emitterRepository;

    private static final long TIMEOUT = 1000L * 60 * 60; // 1시간

    public SseEmitter subscribe(UUID chatroomId, String userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterRepository.save(chatroomId, userId, emitter);

        emitter.onCompletion(() -> emitterRepository.delete(chatroomId, userId));
        emitter.onTimeout(() -> emitterRepository.delete(chatroomId, userId));
        emitter.onError(e -> emitterRepository.delete(chatroomId, userId));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (Exception ignored) {}

        return emitter;
    }


    public void sendChatMessage(UUID chatroomId, Chatting chatData) {
        String json = String.format(
                "{\"senderId\":\"%s\", \"content\":\"%s\"}",
                chatData.getSenderId(),
                chatData.getContent()
        );

        emitterRepository.send(chatroomId, json);
    }

}
