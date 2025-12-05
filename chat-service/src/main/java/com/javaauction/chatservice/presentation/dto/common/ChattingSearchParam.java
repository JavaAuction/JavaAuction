package com.javaauction.chatservice.presentation.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChattingSearchParam {
    private String senderId;
    private String receiverId;
    private Boolean isRead;
    private String content;
}
