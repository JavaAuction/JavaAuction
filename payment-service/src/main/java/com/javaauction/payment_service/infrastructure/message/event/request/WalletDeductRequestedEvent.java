package com.javaauction.payment_service.infrastructure.message.event.request;

import com.javaauction.payment_service.domain.enums.TransactionType;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WalletDeductRequestedEvent {

    private String userId;
    private TransactionType transactionType;
    private Long deductAmount;
    private UUID auctionId;
    private UUID bidId;
}
