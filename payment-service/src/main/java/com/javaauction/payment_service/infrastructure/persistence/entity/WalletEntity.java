package com.javaauction.payment_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Entity
@Table(name = "p_wallet")
@DynamicInsert
@DynamicUpdate
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "wallet_id", nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Builder.Default
    @Column(nullable = false)
    private Long balance = 0L;

    public enum Type {
        CHARGE,
        WITHDRAW
    }
}
