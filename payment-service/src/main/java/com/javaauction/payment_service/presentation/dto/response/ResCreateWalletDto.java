package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.model.Wallet;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ResCreateWalletDto {

    private WalletDto wallet;

    public static ResCreateWalletDto from(WalletDto walletDto) {
        return ResCreateWalletDto.builder()
                .wallet(walletDto)
                .build();
    }

    @Getter
    @Builder
    public static class WalletDto {

        private UUID walletId;
        private String userId;
        private Long balance;

        public static WalletDto from(Wallet wallet) {
            return WalletDto.builder()
                    .walletId(wallet.getId())
                    .userId(wallet.getUserId())
                    .balance(wallet.getBalance())
                    .build();
        }
    }
}
