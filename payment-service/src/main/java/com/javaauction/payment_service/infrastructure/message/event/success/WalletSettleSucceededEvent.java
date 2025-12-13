package com.javaauction.payment_service.infrastructure.message.event.success;

import com.javaauction.payment_service.domain.enums.TransactionType;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WalletSettleSucceededEvent {

    private UUID auctionId;
    private String buyerId;
    private String sellerId;

    private Long amount;
    private TransactionType transactionType;
}
