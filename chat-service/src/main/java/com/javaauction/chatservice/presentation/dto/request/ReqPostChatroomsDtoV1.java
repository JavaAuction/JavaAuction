package com.javaauction.chatservice.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqPostChatroomsDtoV1 {
    private UUID productId;
    private String chatroomHost;
    private String chatroomGuest;
}
