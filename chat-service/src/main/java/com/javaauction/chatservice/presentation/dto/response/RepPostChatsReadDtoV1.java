package com.javaauction.chatservice.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepPostChatsReadDtoV1 {
    private UUID chatroomId;
    private String receiverId;
    private List<UUID> readChatIds;
    private String message;
}
