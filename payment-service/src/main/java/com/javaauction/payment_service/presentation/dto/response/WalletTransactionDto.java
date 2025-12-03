package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class WalletTransactionDto {
    private UUID walletTransactionId;
    private Long amount;
    private TransactionType transactionType;

    public static WalletTransactionDto from(WalletTransaction walletTransaction) {
        return WalletTransactionDto.builder()
                .walletTransactionId(walletTransaction.getId())
                .transactionType(walletTransaction.getType())
                .amount(walletTransaction.getAmount())
                .build();
    }
}