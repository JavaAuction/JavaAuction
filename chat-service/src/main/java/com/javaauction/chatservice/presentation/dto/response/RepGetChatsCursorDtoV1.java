package com.javaauction.chatservice.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class RepGetChatsCursorDtoV1 {

    private List<RepGetChatsDtoV1> chats;

    private Cursor nextCursor;

    private boolean hasNext;

    @Getter
    @Builder
    public static class Cursor {
        private UUID chattingId;
    }
}

