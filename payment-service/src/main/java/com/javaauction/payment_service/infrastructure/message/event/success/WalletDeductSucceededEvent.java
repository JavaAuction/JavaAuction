package com.javaauction.payment_service.infrastructure.message.event.success;

import com.javaauction.payment_service.domain.enums.TransactionType;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WalletDeductSucceededEvent {

    private UUID auctionId;
    private UUID bidId;
    private String userId;

    private Long deductAmount;
    private UUID walletId;
    private UUID walletTransactionId;
    private TransactionType transactionType;
}
