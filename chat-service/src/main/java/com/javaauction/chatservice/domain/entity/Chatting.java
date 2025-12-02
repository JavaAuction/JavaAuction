package com.javaauction.chatservice.domain.entity;

import com.javaauction.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_chatting")
public class Chatting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "chatting_id", nullable = false)
    private UUID chattingId;

    @ManyToOne(fetch = FetchType.EAGER)
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

    public void chattingRead() {
        this.isRead = true;
    }
}