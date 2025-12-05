package com.javaauction.chatservice.application.service;

import com.javaauction.chatservice.application.client.ProductClientV1;
import com.javaauction.chatservice.domain.entity.Chatroom;
import com.javaauction.chatservice.domain.entity.Chatting;
import com.javaauction.chatservice.infrastructure.repository.ChatroomJpaRepository;
import com.javaauction.chatservice.infrastructure.repository.ChattingJpaRepository;
import com.javaauction.chatservice.presentation.advice.ChatErrorCode;
import com.javaauction.chatservice.presentation.dto.common.ChatroomSearchParam;
import com.javaauction.chatservice.presentation.dto.common.ChattingSearchParam;
import com.javaauction.chatservice.presentation.dto.request.ReqPostChatroomsDtoV1;
import com.javaauction.chatservice.presentation.dto.request.ReqPostChatsDtoV1;
import com.javaauction.chatservice.presentation.dto.response.*;
import com.javaauction.global.presentation.exception.BussinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceV1 {
    private final ChatroomJpaRepository chatroomRepository;
    private final ChattingJpaRepository chattingRepository;
    private final ProductClientV1 productClient;

    // 채팅방 생성
    @Transactional
    public RepPostChatroomsDtoV1 postChatrooms(ReqPostChatroomsDtoV1 reqPostChatroomsDto, String userId) {

        RepGetProductsDtoV1 product = productClient.getProduct(reqPostChatroomsDto.getProductId()).getBody().getData();

        // 상품 등록자와 채팅 요청을 보내는 상대가 같은 사람인지 확인
        if (!product.userId().equals(reqPostChatroomsDto.getChatroomHost())) {
            throw new BussinessException(ChatErrorCode.CHAT_PRODUCT_FORBIDDEN);
        }

        // 자기 자신과 채팅할 수 없음
        if (reqPostChatroomsDto.getChatroomHost().equals(userId)) {
            throw new BussinessException(ChatErrorCode.CHAT_CANNOT_CREATE_WITH_SELF);

        }

        // 채팅방 중복 체크(같은 상품ID + 채팅방 호스트 + 채팅방 게스트 조합이 이미 있는지)
        boolean exists = chatroomRepository
                .existsByProductIdAndChatroomHostAndChatroomGuestAndDeletedAtIsNull(
                        reqPostChatroomsDto.getProductId(), reqPostChatroomsDto.getChatroomHost(), userId
                );

        if (exists) {
            throw new BussinessException(ChatErrorCode.CHAT_CHATROOM_ALREADY_EXIST);
        }

        Chatroom chatroom = Chatroom.ofNewChatroom(
                reqPostChatroomsDto.getProductId(),
                reqPostChatroomsDto.getChatroomHost(),
                userId
        );

        chatroomRepository.save(chatroom);

        return RepPostChatroomsDtoV1.builder()
                .chatroomId(chatroom.getChatroomId())
                .productId(chatroom.getProductId())
                .chatroomHost(chatroom.getChatroomHost())
                .chatroomGuest(chatroom.getChatroomGuest())
                .createdAt(chatroom.getCreatedAt())
                .build();

    }

    // 채팅 생성
    @Transactional
    public RepPostChatsDtoV1 postChats(UUID chatroomId, ReqPostChatsDtoV1 reqPostChatsDto, String userId) {
        // 채팅방 존재 여부 확인
        Chatroom chatroom = chatroomRepository.findByChatroomIdAndDeletedAtIsNull(chatroomId)
                .orElseThrow(() -> new BussinessException(ChatErrorCode.CHAT_CHATROOM_NOT_FOUND));

        // 자신이 소속된 채팅방인지 확인
        if (!(chatroom.getChatroomHost().equals(userId) || chatroom.getChatroomGuest().equals(userId))) {
            throw new BussinessException(ChatErrorCode.CHAT_UNAUTH);
        }

        // 자기 자신과 채팅할 수 없음
        if (reqPostChatsDto.getReceiverId().equals(userId)) {
            throw new BussinessException(ChatErrorCode.CHAT_CANNOT_CREATE_WITH_SELF);
        }

        // 채팅 수신자가 채팅방에 소속되어있는지 확인
        if (!(chatroom.getChatroomHost().equals(reqPostChatsDto.getReceiverId()) ||
                chatroom.getChatroomGuest().equals(reqPostChatsDto.getReceiverId()))) {
            throw new BussinessException(ChatErrorCode.CHAT_CANNOT_CREATE_DIFF_RECEIVER);
        }

        Chatting chatting = Chatting.ofNewChatting(
                chatroom,
                userId,
                reqPostChatsDto.getReceiverId(),
                reqPostChatsDto.getContent()
        );


        chattingRepository.save(chatting);

        return RepPostChatsDtoV1.builder()
                .chattingId(chatting.getChattingId())
                .chatroomId(chatting.getChatroom().getChatroomId())
                .senderId(chatting.getSenderId())
                .receiverId(chatting.getReceiverId())
                .content(chatting.getContent())
                .isRead(chatting.getIsRead())
                .createdAt(chatting.getCreatedAt())
                .build();

    }

    // 채팅방 리스트 조회
    public Page<RepGetChatroomsDtoV1> getChatrooms(ChatroomSearchParam chatroomSearchParam, Pageable pageable, String userId, String role) {

        Page<RepGetChatroomsDtoV1> page = chatroomRepository.findChatroomPage(chatroomSearchParam, pageable, userId, role);

        return page;
    }

    // 채팅 리스트 조회
    public Page<RepGetChatsDtoV1> getChats(UUID chatroomId, ChattingSearchParam chattingSearchParam, Pageable pageable, String userId, String role) {
        // 채팅방 존재 여부 확인
        Chatroom chatroom = chatroomRepository.findByChatroomIdAndDeletedAtIsNull(chatroomId)
                .orElseThrow(() -> new BussinessException(ChatErrorCode.CHAT_CHATROOM_NOT_FOUND));

        // 일반 회원은 자신이 소속된 채팅방인지 확인
        if (role.equals("USER") && !(chatroom.getChatroomHost().equals(userId) || chatroom.getChatroomGuest().equals(userId))) {
            throw new BussinessException(ChatErrorCode.CHAT_UNAUTH);
        }

        Page<RepGetChatsDtoV1> page = chattingRepository.findChattingPage(chatroomId, chattingSearchParam, pageable, userId, role);

        return page;
    }

    // 채팅 읽음 처리
    @Transactional
    public RepPostChatsReadDtoV1 postChatsRead(UUID chatroomId, String receiverId) {
        // 채팅방 존재 여부 확인
        Chatroom chatroom = chatroomRepository.findByChatroomIdAndDeletedAtIsNull(chatroomId)
                .orElseThrow(() -> new BussinessException(ChatErrorCode.CHAT_CHATROOM_NOT_FOUND));

        // 자신이 소속된 채팅방인지 확인
        if (!(chatroom.getChatroomHost().equals(receiverId) || chatroom.getChatroomGuest().equals(receiverId))) {
            throw new BussinessException(ChatErrorCode.CHAT_UNAUTH);
        }

        // 읽지 않은 채팅 목록 조회
        List<UUID> unreadChatIds =
                chattingRepository.findUnreadChatIds(chatroomId, receiverId);

        // 읽음 처리
        chattingRepository.markChatsAsRead(unreadChatIds);

        return RepPostChatsReadDtoV1.builder()
                .chatroomId(chatroomId)
                .receiverId(receiverId)
                .readChatIds(unreadChatIds)
                .message(unreadChatIds.size() + "건의 읽지 않은 채팅이 읽음 처리 되었습니다.")
                .build();
    }


}
