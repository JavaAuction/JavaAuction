package com.javaauction.chatservice.presentation.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatroomSearchParam {
    private UUID productId;
    private String chatroomHost;
    private String chatroomGuest;
}
