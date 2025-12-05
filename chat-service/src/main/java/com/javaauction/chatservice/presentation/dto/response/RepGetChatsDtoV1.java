package com.javaauction.chatservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
public class RepGetChatsDtoV1 {
    private UUID chattingId;
    private UUID chatroomId;
    private String senderId;
    private String receiverId;
    private Boolean isRead;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "Asia/Seoul")
    private Instant createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "Asia/Seoul")
    private Instant updatedAt;

    @QueryProjection
    public RepGetChatsDtoV1(UUID chattingId, UUID chatroomId, String senderId, String receiverId, Boolean isRead, String content, Instant createdAt, Instant updatedAt) {
        this.chattingId = chattingId;
        this.chatroomId = chatroomId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.isRead = isRead;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
