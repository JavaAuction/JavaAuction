package com.javaauction.chatservice.application.service;

import com.javaauction.chatservice.domain.entity.Chatroom;
import com.javaauction.chatservice.domain.entity.Chatting;
import com.javaauction.chatservice.infrastructure.repository.ChatroomJpaRepository;
import com.javaauction.chatservice.infrastructure.repository.ChattingJpaRepository;
import com.javaauction.chatservice.presentation.dto.advice.ChatErrorCode;
import com.javaauction.chatservice.presentation.dto.request.ReqPostChatroomsDtoV1;
import com.javaauction.chatservice.presentation.dto.request.ReqPostChatsDtoV1;
import com.javaauction.chatservice.presentation.dto.response.RepPostChatroomsDtoV1;
import com.javaauction.chatservice.presentation.dto.response.RepPostChatsDtoV1;
import com.javaauction.global.presentation.exception.BussinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceV1 {
    private final ChatroomJpaRepository chatroomRepository;
    private final ChattingJpaRepository chattingRepository;

    // todo : 유저 정보 받아오기

    // 채팅방 생성
    @Transactional
    public RepPostChatroomsDtoV1 postChatrooms(ReqPostChatroomsDtoV1 reqPostChatroomsDto, String userId) {
        // todo : 상품 존재 여부 확인

        // todo : 상품 등록자와 chatHost가 같은지 확인

        // 자기 자신과 채팅할 수 없음
        if (reqPostChatroomsDto.getChatroomHost().equals(userId)) {
            throw new BussinessException(ChatErrorCode.CHAT_CANNOT_CREATE_WITH_SELF);

        }

        Chatroom chatroom = Chatroom.ofNewChatroom(
                reqPostChatroomsDto.getProductId(),
                reqPostChatroomsDto.getChatroomHost(),
                userId
        );

        chatroomRepository.save(chatroom);

        return new RepPostChatroomsDtoV1(
                chatroom.getChatroomId(),
                chatroom.getProductId(),
                chatroom.getChatroomHost(),
                chatroom.getChatroomGuest(),
                chatroom.getCreatedAt()
        );

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

        return new RepPostChatsDtoV1(
                chatting.getChattingId(),
                chatting.getChatroom().getChatroomId(),
                chatting.getSenderId(),
                chatting.getReceiverId(),
                chatting.getContent(),
                chatting.getIsRead(),
                chatting.getCreatedAt()
        );

    }

}
