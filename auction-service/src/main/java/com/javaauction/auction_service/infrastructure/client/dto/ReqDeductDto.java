package com.javaauction.auction_service.infrastructure.client.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqDeductDto {

    @NotNull
    private String userId;

    @NotNull
    private TransactionType transactionType;

    @NotNull
    @Min(1)
    private Long deductAmount;

    @NotNull
    private UUID externalId;

    public enum TransactionType {
        CHARGE, WITHDRAW, PAYMENT, HOLD
    }
}
