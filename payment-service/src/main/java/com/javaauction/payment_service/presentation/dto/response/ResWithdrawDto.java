package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ResWithdrawDto {

    private UUID walletId;
    private UUID walletTransactionId;
    private Long withdrawAmount;
    private Long beforeBalance;
    private Long afterBalance;
    private TransactionType transactionType;

    public static ResWithdrawDto from(Wallet wallet, WalletTransaction walletTransaction, Long beforeBalance) {
        return ResWithdrawDto.builder()
                .walletId(wallet.getId())
                .walletTransactionId(walletTransaction.getId())
                .withdrawAmount(walletTransaction.getAmount())
                .beforeBalance(beforeBalance)
                .afterBalance(wallet.getBalance())
                .transactionType(walletTransaction.getTransactionType())
                .build();
    }
}
