package com.javaauction.chatservice.application.service;

import com.javaauction.chatservice.application.client.ProductClientV1;
import com.javaauction.chatservice.application.event.sse.SseEmitterService;
import com.javaauction.chatservice.domain.entity.Chatroom;
import com.javaauction.chatservice.domain.entity.Chatting;
import com.javaauction.chatservice.infrastructure.repository.ChatroomJpaRepository;
import com.javaauction.chatservice.infrastructure.repository.ChattingJpaRepository;
import com.javaauction.chatservice.presentation.advice.ChatSuccessCode;
import com.javaauction.chatservice.presentation.dto.common.ChattingSearchParam;
import com.javaauction.chatservice.presentation.dto.request.ReqPostChatroomsDtoV1;
import com.javaauction.chatservice.presentation.dto.request.ReqPostChatsDtoV1;
import com.javaauction.chatservice.presentation.dto.response.RepGetProductsDtoV1;
import com.javaauction.chatservice.presentation.dto.response.RepPostChatroomsDtoV1;
import com.javaauction.chatservice.presentation.dto.response.RepPostChatsReadDtoV1;
import com.javaauction.global.presentation.exception.BussinessException;
import com.javaauction.global.presentation.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceV1Test {

    @InjectMocks
    private ChatServiceV1 chatService;

    @Mock
    private ChatroomJpaRepository chatroomRepository;

    @Mock
    private ChattingJpaRepository chattingRepository;

    @Mock
    private ProductClientV1 productClient;

    @Mock
    private SseEmitterService sseEmitterService;


    @Test
    @DisplayName("채팅방 생성 성공")
    void postChatrooms_success() {
        UUID productId = UUID.randomUUID();
        String host = "seller";
        String guest = "buyer";

        ReqPostChatroomsDtoV1 req =
                new ReqPostChatroomsDtoV1(productId, host);

        RepGetProductsDtoV1 product =
                new RepGetProductsDtoV1(productId,
                        host,
                        "상품명",
                        "설명",
                        "image.jpg",
                        10000L,
                        RepGetProductsDtoV1.ProductStatus.AUCTION_RUNNING,
                        Instant.now(),
                        Instant.now(),
                        null);

        // 상품 정보 가져오기 : ApiResponse.success null 방지를 위해 테스트에서는 임시로 ChatSuccessCode 사용
        given(productClient.getProduct(productId))
                .willReturn(ResponseEntity.ok(
                        ApiResponse.success(ChatSuccessCode.CHAT_FIND_SUCCESS, product)
                ));

        given(chatroomRepository
                .existsByProductIdAndChatroomHostAndChatroomGuestAndDeletedAtIsNull(
                        productId, host, guest))
                .willReturn(false);

        RepPostChatroomsDtoV1 result =
                chatService.postChatrooms(req, guest);

        assertThat(result).isNotNull();
        verify(chatroomRepository).save(any(Chatroom.class));
    }

    @Test
    @DisplayName("자기 자신과 채팅방 생성 시 예외")
    void postChatrooms_fail_self() {
        UUID productId = UUID.randomUUID();
        String user = "user";

        ReqPostChatroomsDtoV1 req =
                new ReqPostChatroomsDtoV1(productId, user);

        RepGetProductsDtoV1 product =
                new RepGetProductsDtoV1(productId,
                        user,
                        "상품명",
                        "설명",
                        "image.jpg",
                        10000L,
                        RepGetProductsDtoV1.ProductStatus.AUCTION_RUNNING,
                        Instant.now(),
                        Instant.now(),
                        null);

        // 상품 정보 가져오기 : ApiResponse.success null 방지를 위해 테스트에서는 임시로 ChatSuccessCode 사용
        given(productClient.getProduct(productId))
                .willReturn(ResponseEntity.ok(ApiResponse.success(ChatSuccessCode.CHAT_FIND_SUCCESS, product)));

        assertThatThrownBy(() ->
                chatService.postChatrooms(req, user))
                .isInstanceOf(BussinessException.class);
    }


    @Test
    @DisplayName("채팅 전송 성공 및 SSE 호출")
    void postChats_success() {
        UUID chatroomId = UUID.randomUUID();
        String sender = "user1";
        String receiver = "user2";

        Chatroom chatroom =
                Chatroom.ofNewChatroom(UUID.randomUUID(), sender, receiver);

        given(chatroomRepository.findByChatroomIdAndDeletedAtIsNull(chatroomId))
                .willReturn(Optional.of(chatroom));

        ReqPostChatsDtoV1 req =
                new ReqPostChatsDtoV1(receiver, "안녕하세요");

        chatService.postChats(chatroomId, req, sender);

        verify(chattingRepository).save(any(Chatting.class));
        verify(sseEmitterService).sendChatMessage(eq(chatroomId), any(Chatting.class));
    }

    @Test
    @DisplayName("채팅방 멤버가 아닌 경우 채팅 전송 실패")
    void postChats_fail_accessDenied() {
        UUID chatroomId = UUID.randomUUID();

        Chatroom chatroom =
                Chatroom.ofNewChatroom(UUID.randomUUID(), "host", "guest");

        given(chatroomRepository.findByChatroomIdAndDeletedAtIsNull(chatroomId))
                .willReturn(Optional.of(chatroom));

        ReqPostChatsDtoV1 req =
                new ReqPostChatsDtoV1("guest", "hi");

        assertThatThrownBy(() ->
                chatService.postChats(chatroomId, req, "other"))
                .isInstanceOf(BussinessException.class);
    }


    @Test
    @DisplayName("채팅 읽음 처리 성공")
    void postChatsRead_success() {
        UUID chatroomId = UUID.randomUUID();
        String receiver = "user";

        Chatroom chatroom =
                Chatroom.ofNewChatroom(UUID.randomUUID(), receiver, "other");

        given(chatroomRepository.findByChatroomIdAndDeletedAtIsNull(chatroomId))
                .willReturn(Optional.of(chatroom));

        given(chattingRepository.findUnreadChatIds(chatroomId, receiver))
                .willReturn(List.of(UUID.randomUUID()));

        RepPostChatsReadDtoV1 result =
                chatService.postChatsRead(chatroomId, receiver);

        assertThat(result.getReadChatIds()).hasSize(1);
        verify(chattingRepository).markChatsAsRead(anyList());
    }


    @Test
    @DisplayName("USER 권한이 채팅방 멤버가 아니면 조회 불가")
    void getChats_fail_userAccessDenied() {
        UUID chatroomId = UUID.randomUUID();

        Chatroom chatroom =
                Chatroom.ofNewChatroom(UUID.randomUUID(), "host", "guest");

        given(chatroomRepository.findByChatroomIdAndDeletedAtIsNull(chatroomId))
                .willReturn(Optional.of(chatroom));

        assertThatThrownBy(() ->
                chatService.getChats(
                        chatroomId,
                        new ChattingSearchParam(null, null, null, null),
                        Pageable.unpaged(),
                        "other",
                        "USER"
                ))
                .isInstanceOf(BussinessException.class);
    }
}
