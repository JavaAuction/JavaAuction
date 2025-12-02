package com.javaauction.payment_service.infrastructure.persistence.entity;

import com.javaauction.global.infrastructure.entity.BaseEntity;
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
public class WalletEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "wallet_id", nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Builder.Default
    @Column(nullable = false)
    private Long balance = 0L;
}
