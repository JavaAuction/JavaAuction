package com.javaauction.payment_service.presentation.dto.request;

import com.javaauction.payment_service.domain.model.WalletTransaction;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqWithdrawDto {

    @NotNull
    private WalletTransaction.TransactionType transactionType;

    @NotNull
    @Min(1)
    private Long amount;

    private WalletTransaction.ExternalType externalType;
    private UUID externalId;
}
