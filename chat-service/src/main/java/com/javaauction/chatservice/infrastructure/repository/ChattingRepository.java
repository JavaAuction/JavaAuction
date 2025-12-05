package com.javaauction.chatservice.infrastructure.repository;

import com.javaauction.chatservice.presentation.dto.common.ChattingSearchParam;
import com.javaauction.chatservice.presentation.dto.response.RepGetChatsDtoV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ChattingRepository {
    // 검색 조건, 페이지 정보 기반 채팅 목록 동적 조회
    Page<RepGetChatsDtoV1> findChattingPage(UUID chatroomId, ChattingSearchParam chattingSearchParam, Pageable pageable, String userId, String role);
}
