package com.javaauction.chatservice.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_chatroom")
public class Chatroom {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "chatroom_id", nullable = false)
    private UUID chatroomId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "chatroom_host", nullable = false)
    private String chatroomHost;

    @Column(name = "chatroom_guest", nullable = false)
    private String chatroomGuest;

    private Chatroom(UUID productId, String chatroomHost, String chatroomGuest) {
        this.productId = productId;
        this.chatroomHost = chatroomHost;
        this.chatroomGuest = chatroomGuest;
    }

    private static Chatroom ofNewChatroom(UUID productId, String chatroomHost, String chatroomGuest) {
        return new Chatroom(productId, chatroomHost, chatroomGuest);
    }

}
