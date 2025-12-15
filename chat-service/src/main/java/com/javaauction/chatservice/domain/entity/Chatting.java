package com.javaauction.chatservice.domain.entity;

import com.javaauction.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "p_chatting",
        indexes = {
                // chatroom별 최신 채팅 조회 (서브쿼리 + ORDER BY)
                @Index(name = "idx_chatting_room_created_desc", columnList = "chatroom_id, created_at DESC"),

                // USER 권한 조회 최적화 (OR 조건 대비)
                @Index(name = "idx_chatting_room_sender_created", columnList = "chatroom_id, sender_id, created_at DESC"),
                @Index(name = "idx_chatting_room_receiver_created", columnList = "chatroom_id, receiver_id, created_at DESC")
        }
)
public class Chatting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "chatting_id", nullable = false)
    private UUID chattingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", referencedColumnName = "chatroom_id", nullable = false)
    private Chatroom chatroom;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "receiver_id", nullable = false)
    private String receiverId;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "content", nullable = false)
    private String content;

    private Chatting(Chatroom chatroom, String senderId, String receiverId, String content) {
        this.chatroom = chatroom;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.isRead = false;
    }

    public static Chatting ofNewChatting(Chatroom chatroom, String senderId, String receiverId, String content) {
        return new Chatting(chatroom, senderId, receiverId, content);
    }

}