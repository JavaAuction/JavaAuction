package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.model.Wallet;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ResCreateWalletDto {

    private UUID walletId;
    private String userId;
    private Long balance;

    public static ResCreateWalletDto from(Wallet wallet) {
        return ResCreateWalletDto.builder()
                .walletId(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .build();
    }
}
