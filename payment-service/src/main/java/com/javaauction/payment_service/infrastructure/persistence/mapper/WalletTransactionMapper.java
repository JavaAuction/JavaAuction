package com.javaauction.payment_service.infrastructure.persistence.mapper;

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
                .type(toDomain(entity.getType()))
                .amount(entity.getAmount())
                .holdStatus(toDomain(entity.getHoldStatus()))
                .externalType(toDomain(entity.getExternalType()))
                .externalId(entity.getExternalId())
                .build();
    }

    public WalletTransactionEntity toEntity(WalletTransaction walletTransaction, WalletEntity walletEntity) {
        if (walletTransaction == null) return null;

        return WalletTransactionEntity.builder()
                .id(walletTransaction.getId())
                .wallet(walletEntity)
                .type(toEntity(walletTransaction.getType()))
                .amount(walletTransaction.getAmount())
                .holdStatus(toEntity(walletTransaction.getHoldStatus()))
                .externalType(toEntity(walletTransaction.getExternalType()))
                .externalId(walletTransaction.getExternalId())
                .build();
    }

    private WalletTransaction.TransactionType toDomain(WalletTransactionEntity.TransactionType type) {
        return type == null ? null : WalletTransaction.TransactionType.valueOf(type.name());
    }

    private WalletTransactionEntity.TransactionType toEntity(WalletTransaction.TransactionType type) {
        return type == null ? null : WalletTransactionEntity.TransactionType.valueOf(type.name());
    }

    private WalletTransaction.HoldStatus toDomain(WalletTransactionEntity.HoldStatus status) {
        return status == null ? null : WalletTransaction.HoldStatus.valueOf(status.name());
    }

    private WalletTransactionEntity.HoldStatus toEntity(WalletTransaction.HoldStatus status) {
        return status == null ? null : WalletTransactionEntity.HoldStatus.valueOf(status.name());
    }

    private WalletTransaction.ExternalType toDomain(WalletTransactionEntity.ExternalType type) {
        return type == null ? null : WalletTransaction.ExternalType.valueOf(type.name());
    }

    private WalletTransactionEntity.ExternalType toEntity(WalletTransaction.ExternalType type) {
        return type == null ? null : WalletTransactionEntity.ExternalType.valueOf(type.name());
    }
}
