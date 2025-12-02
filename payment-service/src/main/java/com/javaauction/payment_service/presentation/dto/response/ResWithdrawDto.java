package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
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
    private WalletTransaction.TransactionType transactionType;

    public static ResWithdrawDto from(Wallet wallet, WalletTransaction walletTransaction, Long beforeWithdraw) {
        return ResWithdrawDto.builder()
                .walletId(wallet.getId())
                .withdrawAmount(walletTransaction.getAmount())
                .beforeWithdraw(beforeWithdraw)
                .afterWithdraw(wallet.getBalance())
                .transactionType(walletTransaction.getType())
                .build();
    }
}
