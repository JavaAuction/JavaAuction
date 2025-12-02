package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.model.Wallet;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class WalletDto {
    private UUID walletId;
    private Long beforeBalance;
    private Long afterBalance;

    public static WalletDto from(Wallet wallet, Long beforeBalance) {
        return WalletDto.builder()
                .walletId(wallet.getId())
                .beforeBalance(beforeBalance)
                .afterBalance(wallet.getBalance())
                .build();
    }
}