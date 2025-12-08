package com.javaauction.chatservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaauction.chatservice.domain.entity.Chatting;
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
@AllArgsConstructor
public class RepPostChatsDtoV1 {

    private UUID chattingId;
    private UUID chatroomId;
    private String senderId;
    private String receiverId;
    private String content;
    private Boolean isRead;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "Asia/Seoul")
    private Instant createdAt;

    public static RepPostChatsDtoV1 from(Chatting chatting) {
        return RepPostChatsDtoV1.builder()
                .chattingId(chatting.getChattingId())
                .chatroomId(chatting.getChatroom().getChatroomId())
                .senderId(chatting.getSenderId())
                .receiverId(chatting.getReceiverId())
                .content(chatting.getContent())
                .isRead(chatting.getIsRead())
                .createdAt(chatting.getCreatedAt())
                .build();
    }
}
