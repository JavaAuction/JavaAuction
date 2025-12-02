package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.model.Wallet;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ResChargeDto {

    private UUID walletId;
    private Long chargeAmount;
    private Long beforeCharge;
    private Long afterCharge;

    public static ResChargeDto from(Wallet wallet, Long chargeAmount, Long beforeCharge) {
        return ResChargeDto.builder()
                .walletId(wallet.getId())
                .chargeAmount(chargeAmount)
                .beforeCharge(beforeCharge)
                .afterCharge(wallet.getBalance())
                .build();
    }
}
