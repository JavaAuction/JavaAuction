package com.javaauction.payment_service.infrastructure.persistence.entity;

import com.javaauction.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_wallet_transaction")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
public class WalletTransactionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "wallet_transaction_id", nullable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletEntity wallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    private HoldStatus holdStatus;

    @Enumerated(EnumType.STRING)
    private ExternalType externalType;

    private UUID externalId;

    public enum TransactionType {
        CHARGE, WITHDRAW, PAYMENT, HOLD
    }

    public enum HoldStatus {
        HOLD_ACTIVE, HOLD_RELEASED, HOLD_CAPTURED
    }

    public enum ExternalType {
        AUCTION, BID
    }

}
