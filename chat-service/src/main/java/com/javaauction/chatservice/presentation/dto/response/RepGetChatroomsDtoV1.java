package com.javaauction.chatservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepGetChatroomsDtoV1 {
    private UUID chatroomId;
    private String productName;
    private String chatroomHost;
    private String chatroomGuest;
    private String lastMessage;
    private Boolean isRead;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "Asia/Seoul")
    private Instant createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "Asia/Seoul")
    private Instant updatedAt;

}
