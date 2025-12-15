package com.javaauction.auction_service.domain.event;

import com.javaauction.auction_service.infrastructure.client.dto.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class WalletDeductSucceededEvent {

    private final UUID auctionId;
    private final UUID bidId;
    private final String userId;

    private final Long deductAmount;
    private final UUID walletId;
    private final UUID walletTransactionId;
    private final TransactionType transactionType;
}
