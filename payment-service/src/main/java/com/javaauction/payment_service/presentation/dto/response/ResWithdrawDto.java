package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ResWithdrawDto {

    private WalletDto wallet;
    private WalletTransactionDto walletTransaction;

    public static ResWithdrawDto from(WalletDto wallet, WalletTransactionDto walletTransaction) {
        return ResWithdrawDto.builder()
                .wallet(wallet)
                .walletTransaction(walletTransaction)
                .build();
    }

    @Getter
    @Builder
    public static class WalletDto {
        private UUID walletId;
        private Long beforeWithdraw;
        private Long afterWithdraw;

        public static WalletDto from(Wallet wallet, Long beforeWithdraw) {
            return WalletDto.builder()
                    .walletId(wallet.getId())
                    .beforeWithdraw(beforeWithdraw)
                    .afterWithdraw(wallet.getBalance())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class WalletTransactionDto {
        private UUID walletTransactionId;
        private Long withdrawAmount;
        private WalletTransaction.TransactionType transactionType;

        public static WalletTransactionDto from(WalletTransaction walletTransaction) {
            return WalletTransactionDto.builder()
                    .walletTransactionId(walletTransaction.getId())
                    .transactionType(walletTransaction.getType())
                    .withdrawAmount(walletTransaction.getAmount())
                    .build();
        }
    }
}
