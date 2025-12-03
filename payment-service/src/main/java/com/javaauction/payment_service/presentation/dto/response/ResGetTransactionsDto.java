package com.javaauction.payment_service.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.javaauction.payment_service.domain.enums.HoldStatus;
import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResGetTransactionsDto {

    private UUID transactionId;
    private Long amount;
    private TransactionType transactionType;
    private HoldStatus holdStatus;

    public static ResGetTransactionsDto from(WalletTransaction walletTransaction) {
        return ResGetTransactionsDto.builder()
                .transactionId(walletTransaction.getId())
                .amount(walletTransaction.getAmount())
                .transactionType(walletTransaction.getTransactionType())
                .holdStatus(walletTransaction.getHoldStatus())
                .build();
    }
}
