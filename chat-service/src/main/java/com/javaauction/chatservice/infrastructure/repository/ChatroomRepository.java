package com.javaauction.chatservice.infrastructure.repository;

import com.javaauction.chatservice.presentation.dto.common.ChatroomSearchParam;
import com.javaauction.chatservice.presentation.dto.response.RepGetChatroomsDtoV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatroomRepository {
    // 검색 조건, 페이지 정보 기반 채팅방 목록 동적 조회
    Page<RepGetChatroomsDtoV1> findChatroomPage(ChatroomSearchParam chatroomSearchParam, Pageable pageable, String userId, String role);
}
