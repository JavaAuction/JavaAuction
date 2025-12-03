package com.javaauction.payment_service.infrastructure.persistence.mapper;

import com.javaauction.payment_service.domain.enums.ExternalType;
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

    private ExternalType toDomain(ExternalType type) {
        return type == null ? null : ExternalType.valueOf(type.name());
    }

    private ExternalType toEntity(ExternalType type) {
        return type == null ? null : ExternalType.valueOf(type.name());
    }
}
