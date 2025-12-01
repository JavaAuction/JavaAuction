package com.javaauction.payment_service.presentation.dto.request;

import com.javaauction.payment_service.domain.model.Wallet;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqChargeDto {

    @NotNull
    private Long amount;
}
