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
public class ReqDeductDto {

    @NotNull
    private String userId;

    @NotNull
    private WalletTransaction.TransactionType transactionType;

    @NotNull
    @Min(1)
    private Long deductAmount;

    @NotNull
    private UUID externalId;
}
