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
public class ReqHoldDto {

    @NotNull
    private String userId;

    @NotNull
    @Min(1)
    private Long amount;

    @NotNull
    private UUID bidId;
}
