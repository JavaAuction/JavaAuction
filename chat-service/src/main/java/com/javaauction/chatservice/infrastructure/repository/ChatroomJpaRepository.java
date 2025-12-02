package com.javaauction.chatservice.infrastructure.repository;

import com.javaauction.chatservice.domain.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatroomJpaRepository extends JpaRepository<Chatroom, UUID>, ChatroomRepository {
    // 채팅방 존재 여부 확인
    Optional<Chatroom> findByChatroomIdAndDeletedAtIsNull(UUID chatroomId);
}
