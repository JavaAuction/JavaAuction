package com.javaauction.chatservice.domain.entity;

import com.javaauction.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "p_chatroom",
        indexes = {
                // USER 권한: 내가 속한 채팅방 조회 (OR 조건 대비)
                @Index(name = "idx_chatroom_host_deleted", columnList = "chatroom_host, deleted_at"),
                @Index(name = "idx_chatroom_guest_deleted", columnList = "chatroom_guest, deleted_at"),

                // 상품 기준 조회
                @Index(name = "idx_chatroom_product", columnList = "product_id"),

                // 기본 정렬
                @Index(name = "idx_chatroom_created_at", columnList = "created_at DESC")
        }
)
public class Chatroom extends BaseEntity {
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

    public static Chatroom ofNewChatroom(UUID productId, String chatroomHost, String chatroomGuest) {
        return new Chatroom(productId, chatroomHost, chatroomGuest);
    }

}
