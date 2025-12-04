package com.javaauction.payment_service.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.javaauction.payment_service.domain.enums.ExternalType;
import com.javaauction.payment_service.domain.enums.HoldStatus;
import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResGetTransactionDto {

    private UUID walletTransactionId;
    private Long amount;
    private TransactionType transactionType;
    private HoldStatus holdStatus;
    private ExternalType externalType;
    private UUID externalId;

    public static ResGetTransactionDto fromDomain(WalletTransaction walletTransaction) {
        return ResGetTransactionDto.builder()
                .walletTransactionId(walletTransaction.getId())
                .amount(walletTransaction.getAmount())
                .transactionType(walletTransaction.getTransactionType())
                .holdStatus(walletTransaction.getHoldStatus())
                .externalType(walletTransaction.getExternalType())
                .externalId(walletTransaction.getExternalId())
                .build();
    }
}
