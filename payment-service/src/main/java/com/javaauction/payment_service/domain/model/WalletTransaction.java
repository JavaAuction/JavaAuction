package com.javaauction.payment_service.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@EqualsAndHashCode(of = "id")
public class WalletTransaction {

    private final UUID id;
    private final UUID walletId;
    private final TransactionType type;
    private final Long amount;
    private final HoldStatus holdStatus;
    private final ExternalType externalType;
    private final UUID externalId;

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
