package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.model.Wallet;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ResWithdrawDto {

    private UUID walletId;
    private Long withdrawAmount;
    private Long beforeWithdraw;
    private Long afterWithdraw;

    public static ResWithdrawDto from(Wallet wallet, Long withdrawAmount, Long beforeWithdraw) {
        return ResWithdrawDto.builder()
                .walletId(wallet.getId())
                .withdrawAmount(withdrawAmount)
                .beforeWithdraw(beforeWithdraw)
                .afterWithdraw(wallet.getBalance())
                .build();
    }
}
