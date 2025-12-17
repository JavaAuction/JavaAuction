package com.javaauction.chatservice.presentation.controller;

import com.javaauction.chatservice.application.service.ChatServiceV1;
import com.javaauction.chatservice.presentation.advice.ChatSuccessCode;
import com.javaauction.chatservice.presentation.dto.common.ChatroomSearchParam;
import com.javaauction.chatservice.presentation.dto.common.ChattingSearchParam;
import com.javaauction.chatservice.presentation.dto.request.ReqPostChatroomsDtoV1;
import com.javaauction.chatservice.presentation.dto.request.ReqPostChatsDtoV1;
import com.javaauction.chatservice.presentation.dto.response.*;
import com.javaauction.global.presentation.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Tag(
        name = "채팅 서비스 API",
        description = "상품과 관련된 1:1 채팅방 및 채팅 메시지를 관리하는 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chatrooms")
public class ChatControllerV1 {
    private final ChatServiceV1 chatService;

    @Operation(
            summary = "채팅방 생성",
            description = """
        특정 상품을 기준으로 사용자 간 1:1 채팅방을 생성합니다.
        """
    )
    // 채팅방 생성
    @PostMapping
    public ResponseEntity<ApiResponse<RepPostChatroomsDtoV1>> createChatroom(@RequestBody ReqPostChatroomsDtoV1 reqDto,
                                                                             @RequestHeader("X-User-Username") String username) {
        RepPostChatroomsDtoV1 postChatroomsDto = chatService.postChatrooms(reqDto, username);
        return ResponseEntity.ok(ApiResponse.success(ChatSuccessCode.CHAT_CREATE_SUCCESS, postChatroomsDto));
    }

    @Operation(
            summary = "채팅 메시지 전송",
            description = """
        채팅방에 메시지를 전송합니다.
        전송된 메시지는 실시간 SSE 구독자에게 전달됩니다.
        """
    )
    // 채팅 전송
    @PostMapping("/{chatroomId}/chats")
    public ResponseEntity<ApiResponse<RepPostChatsDtoV1>> createChats(@PathVariable UUID chatroomId, @RequestBody ReqPostChatsDtoV1 reqDto,
                                                                      @RequestHeader("X-User-Username") String username) {
        RepPostChatsDtoV1 postChatsDto = chatService.postChats(chatroomId, reqDto, username);
        return ResponseEntity.ok(ApiResponse.success(ChatSuccessCode.CHAT_CREATE_SUCCESS, postChatsDto));
    }

    @Operation(
            summary = "채팅방 목록 조회",
            description = """
        로그인한 사용자가 참여한 채팅방 목록을 조회합니다.
        상품 ID 또는 참여 사용자 기준으로 필터링할 수 있습니다.
        """
    )
    // 채팅방 리스트 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RepGetChatroomsDtoV1>>> getChatrooms(
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) String chatroomHost,
            @RequestParam(required = false) String chatroomGuest,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestHeader("X-User-Username") String username,
            @RequestHeader("X-User-Role") String role
    ) {

        ChatroomSearchParam searchParam = new ChatroomSearchParam(productId, chatroomHost, chatroomGuest);
        Page<RepGetChatroomsDtoV1> getChatroomsDto = chatService.getChatrooms(searchParam, pageable, username, role);

        return ResponseEntity.ok(
                ApiResponse.success(ChatSuccessCode.CHAT_FIND_SUCCESS, getChatroomsDto)
        );
    }

    @Operation(
            summary = "채팅 메시지 목록 조회",
            description = """
        특정 채팅방의 메시지 목록을 페이지 단위로 조회합니다.
        읽음 여부 및 메시지 내용으로 필터링할 수 있습니다.
        """
    )
    // 채팅 리스트 조회
    @GetMapping("/{chatroomId}/chats")
    public ResponseEntity<ApiResponse<Page<RepGetChatsDtoV1>>> getChats(
            @PathVariable UUID chatroomId,
            @RequestParam(required = false) String chatroomHostId,
            @RequestParam(required = false) String chatroomGuestId,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) String content,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader("X-User-Username") String username,
            @RequestHeader("X-User-Role") String role
    ) {

        ChattingSearchParam searchParam = new ChattingSearchParam(chatroomHostId, chatroomGuestId, isRead, content);
        Page<RepGetChatsDtoV1> getChatsDto = chatService.getChats(chatroomId, searchParam, pageable, username, role);

        return ResponseEntity.ok(
                ApiResponse.success(ChatSuccessCode.CHAT_FIND_SUCCESS, getChatsDto)
        );
    }

    @Operation(
            summary = "채팅 메시지 읽음 처리",
            description = """
        채팅방 내에서 상대방이 보낸 메시지를 읽음 상태로 변경합니다.
        """
    )
    // 채팅 읽음 처리
    @PostMapping("/{chatroomId}/chats/read")
    public ResponseEntity<ApiResponse<RepPostChatsReadDtoV1>> readChats(@PathVariable UUID chatroomId,
                                                                        @RequestHeader("X-User-Username") String username) {

        RepPostChatsReadDtoV1 postChatsReadDto = chatService.postChatsRead(chatroomId, username);

        return ResponseEntity.ok(
                ApiResponse.success(ChatSuccessCode.CHAT_FIND_SUCCESS, postChatsReadDto)
        );
    }

    @Operation(
            summary = "채팅 실시간 구독 (SSE)",
            description = """
        채팅방의 메시지를 실시간으로 수신하기 위한 SSE 구독 API입니다.
        연결이 유지되는 동안 새 메시지가 즉시 전달됩니다.
        """
    )
    // SSE 구독
    @GetMapping(value="/{chatroomId}/subscribe", produces="text/event-stream")
    public SseEmitter subscribe(
            @PathVariable UUID chatroomId,
            @RequestHeader("X-User-Username") String userId,
            @RequestHeader("X-User-Role") String role
    ) {
        return chatService.subscribeChatroom(chatroomId, userId, role);
    }

    @Operation(
            summary = "채팅 메시지 목록 조회 (커서 기반)",
            description = """
        커서 기반 페이징 방식으로 채팅 메시지를 조회합니다.
        무한 스크롤 UI에 사용되며 이전 메시지를 순차적으로 불러옵니다.
        """
    )
    // 커서 기반 채팅 리스트 조회
    @GetMapping("/{chatroomId}/chats/cursor")
    public ResponseEntity<ApiResponse<RepGetChatsCursorDtoV1>> getChatsByCursor(
            @PathVariable UUID chatroomId,
            @RequestParam(required = false) UUID cursorChattingId,
            @RequestHeader("X-User-Username") String username,
            @RequestHeader("X-User-Role") String role
    ) {
        RepGetChatsCursorDtoV1 result =
                chatService.getChatsByCursor(
                        chatroomId,
                        cursorChattingId,
                        username,
                        role
                );

        return ResponseEntity.ok(
                ApiResponse.success(ChatSuccessCode.CHAT_FIND_SUCCESS, result)
        );
    }

}
