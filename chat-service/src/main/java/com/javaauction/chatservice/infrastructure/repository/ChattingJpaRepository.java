package com.javaauction.chatservice.infrastructure.repository;

import com.javaauction.chatservice.domain.entity.Chatting;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ChattingJpaRepository extends JpaRepository<Chatting, UUID>, ChattingRepository{


    // 읽지 않은 채팅 리스트 받아오기
    @Query("""
        select c.chattingId
        from Chatting c
        where c.chatroom.chatroomId = :chatroomId
          and c.receiverId = :receiverId
          and c.isRead = false
          and c.deletedAt is null
    """)
    List<UUID> findUnreadChatIds(@Param("chatroomId") UUID chatroomId,
                                 @Param("receiver") String receiverId);


    // 채팅 읽음 처리 하기
    @Modifying
    @Query("""
        update Chatting c
        set c.isRead = true,
            c.updatedAt = CURRENT_TIMESTAMP
        where c.chattingId in :chatIds
    """)
    int markChatsAsRead(@Param("chatIds") List<UUID> chatIds);

}
