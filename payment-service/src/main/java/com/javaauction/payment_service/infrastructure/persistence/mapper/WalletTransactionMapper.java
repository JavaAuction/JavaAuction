package com.javaauction.payment_service.infrastructure.persistence.mapper;

import com.javaauction.payment_service.domain.enums.HoldStatus;
import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import com.javaauction.payment_service.infrastructure.persistence.entity.WalletEntity;
import com.javaauction.payment_service.infrastructure.persistence.entity.WalletTransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class WalletTransactionMapper {

    public WalletTransaction toDomain(WalletTransactionEntity entity) {
        if (entity == null) return null;

        return WalletTransaction.builder()
                .id(entity.getId())
                .walletId(entity.getWallet().getId())
                .transactionType(toDomain(entity.getTransactionType()))
                .amount(entity.getAmount())
                .holdStatus(toDomain(entity.getHoldStatus()))
                .auctionId(entity.getAuctionId())
                .bidId(entity.getBidId())
                .build();
    }

    public WalletTransactionEntity toEntity(WalletTransaction walletTransaction, WalletEntity walletEntity) {
        if (walletTransaction == null) return null;

        return WalletTransactionEntity.builder()
                .id(walletTransaction.getId())
                .wallet(walletEntity)
                .transactionType(toEntity(walletTransaction.getTransactionType()))
                .amount(walletTransaction.getAmount())
                .holdStatus(toEntity(walletTransaction.getHoldStatus()))
                .auctionId(walletTransaction.getAuctionId())
                .bidId(walletTransaction.getBidId())
                .build();
    }

    private TransactionType toDomain(TransactionType type) {
        return type == null ? null : TransactionType.valueOf(type.name());
    }

    private TransactionType toEntity(TransactionType type) {
        return type == null ? null : TransactionType.valueOf(type.name());
    }

    private HoldStatus toDomain(HoldStatus status) {
        return status == null ? null : HoldStatus.valueOf(status.name());
    }

    private HoldStatus toEntity(HoldStatus status) {
        return status == null ? null : HoldStatus.valueOf(status.name());
    }
}
