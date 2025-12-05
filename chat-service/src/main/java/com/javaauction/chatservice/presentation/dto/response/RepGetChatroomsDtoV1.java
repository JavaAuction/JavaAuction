package com.javaauction.chatservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class RepGetChatroomsDtoV1 {
    private UUID chatroomId;
    private UUID productId;
    private String chatroomHost;
    private String chatroomGuest;
    private String lastMessage;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "Asia/Seoul")
    private Instant createdAt;

    @QueryProjection
    public RepGetChatroomsDtoV1(UUID chatroomId, UUID productId, String chatroomHost, String chatroomGuest, String lastMessage, Instant createdAt) {
        this.chatroomId = chatroomId;
        this.productId = productId;
        this.chatroomHost = chatroomHost;
        this.chatroomGuest = chatroomGuest;
        this.lastMessage = lastMessage;
        this.createdAt = createdAt;
    }

}
