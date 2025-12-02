package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
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
    private WalletTransaction.TransactionType transactionType;

    public static ResChargeDto from(Wallet wallet, WalletTransaction walletTransaction, Long beforeCharge) {
        return ResChargeDto.builder()
                .walletId(wallet.getId())
                .chargeAmount(walletTransaction.getAmount())
                .beforeCharge(beforeCharge)
                .afterCharge(wallet.getBalance())
                .transactionType(walletTransaction.getType())
                .build();
    }
}
