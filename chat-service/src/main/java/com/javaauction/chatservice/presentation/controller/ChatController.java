package com.javaauction.chatservice.presentation.controller;

import com.javaauction.chatservice.application.service.ChatServiceV1;
import com.javaauction.chatservice.presentation.dto.advice.ChatSuccessCode;
import com.javaauction.chatservice.presentation.dto.request.ReqPostChatroomsDtoV1;
import com.javaauction.chatservice.presentation.dto.request.ReqPostChatsDtoV1;
import com.javaauction.chatservice.presentation.dto.response.*;
import com.javaauction.global.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chatrooms")
public class ChatController {
    private final ChatServiceV1 chatService;
    // 채팅방 생성
    @PostMapping
    public ResponseEntity<ApiResponse<RepPostChatroomsDtoV1>> createChatroom(@RequestBody ReqPostChatroomsDtoV1 reqDto) {
        RepPostChatroomsDtoV1 postChatroomsDto = chatService.postChatrooms(reqDto, "tmpuser2"); // 임시로 userId 지정
        return ResponseEntity.ok(ApiResponse.success(ChatSuccessCode.CHAT_CREATE_SUCCESS, postChatroomsDto));
    }

    // 채팅 전송
    @PostMapping("/{chatroomId}/chats")
    public ResponseEntity<ApiResponse<RepPostChatsDtoV1>> createChats(@PathVariable UUID chatroomId, @RequestBody ReqPostChatsDtoV1 reqDto) {
        RepPostChatsDtoV1 postChatsDto = chatService.postChats(chatroomId, reqDto, "tmpuser2"); // 임시로 userId 지정
        return ResponseEntity.ok(ApiResponse.success(ChatSuccessCode.CHAT_CREATE_SUCCESS, postChatsDto));
    }


    // 채팅방 리스트 조회
    @GetMapping
    public ResponseEntity<Page<RepGetChatroomsDtoV1>> getChatrooms(
            @PageableDefault(size = 10) Pageable pageable
    ) {

        RepGetChatroomsDtoV1 response = new RepGetChatroomsDtoV1(
                UUID.randomUUID(),
                "샘플 상품명",
                "chatroomHost",
                "chatroomGuest",
                "그냥 무나해주시면 안되나요?",
                false,
                Instant.now(),
                Instant.now()
        );
        List<RepGetChatroomsDtoV1> chatrooms = List.of(response);
        Page<RepGetChatroomsDtoV1> chatroomsDtoV1Page = new PageImpl<>(chatrooms, pageable, 0);

        return ResponseEntity.ok(chatroomsDtoV1Page);
    }


    // 채팅 리스트 조회
    @GetMapping("/{chatroomId}/chats")
    public ResponseEntity<Page<RepGetChatsDtoV1>> getChats(
            @PathVariable UUID chatroomId,
            @PageableDefault(size = 10) Pageable pageable
    ) {

        RepGetChatsDtoV1 response = new RepGetChatsDtoV1(
                UUID.randomUUID(),
                chatroomId,
                "senderId",
                "receiverId",
                true,
                "이 정도 물건도 경매에 나와요?",
                Instant.now(),
                Instant.now()
        );
        List<RepGetChatsDtoV1> chats = List.of(response);
        Page<RepGetChatsDtoV1> chatsDtoV1Page = new PageImpl<>(chats, pageable, 0);

        return ResponseEntity.ok(chatsDtoV1Page);
    }


    // 채팅 읽음 처리
    @PostMapping("/{chatroomId}/chats/read")
    public ResponseEntity<RepPostChatsReadDtoV1> readChats(@PathVariable UUID chatroomId) {

        String receiverId = "dummyReceiver";

        // 더미 메시지 리스트
        List<RepPostChatsDtoV1> chatList = List.of(
                new RepPostChatsDtoV1(UUID.randomUUID(), chatroomId, "sender1", receiverId, "안녕하세요", false, Instant.now()),
                new RepPostChatsDtoV1(UUID.randomUUID(), chatroomId, "sender2", receiverId, "혹시 이 물건 아직 남아있나요?", false, Instant.now())
        );

        // 읽음 처리 (더미 환경이므로 리스트 변환)
        List<RepPostChatsDtoV1> readChats = chatList.stream()
                .map(chat -> RepPostChatsDtoV1.builder()
                        .chattingId(chat.getChattingId())
                        .chatroomId(chat.getChatroomId())
                        .senderId(chat.getSenderId())
                        .receiverId(chat.getReceiverId())
                        .content(chat.getContent())
                        .isRead(true)
                        .createdAt(chat.getCreatedAt())
                        .build()
                ).toList();

        // 읽음 처리 & 읽은 chatId 추출
        List<UUID> readChatIds = chatList.stream()
                .map(chat -> chat.getChattingId())
                .toList();

        RepPostChatsReadDtoV1 response = RepPostChatsReadDtoV1.builder()
                .chatroomId(chatroomId)
                .receiverId(receiverId)
                .readChatIds(readChatIds)
                .message("읽지 않은 채팅 " + readChats.size() + "건이 읽음 처리 되었습니다.")
                .build();

        return ResponseEntity.ok(response);
    }
}
