package com.javaauction.payment_service.domain.model;

import com.javaauction.payment_service.domain.enums.HoldStatus;
import com.javaauction.payment_service.domain.enums.TransactionType;
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
    private final TransactionType transactionType;
    private final Long amount;
    private final HoldStatus holdStatus;
    private final UUID auctionId;
    private final UUID bidId;
}
