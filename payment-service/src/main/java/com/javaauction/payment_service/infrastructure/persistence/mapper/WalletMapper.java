package com.javaauction.payment_service.infrastructure.persistence.mapper;

import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.infrastructure.persistence.entity.WalletEntity;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public Wallet toDomain(WalletEntity entity) {
        if (entity == null) return null;

        return Wallet.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(toDomain(entity.getType()))
                .balance(entity.getBalance())
                .build();
    }

    public WalletEntity toEntity(Wallet wallet) {
        if (wallet == null) return null;

        return WalletEntity.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .type(toEntity(wallet.getType()))
                .balance(wallet.getBalance())
                .build();
    }

    private Wallet.Type toDomain(WalletEntity.Type type) {
        return type == null ? null : Wallet.Type.valueOf(type.name());
    }

    private WalletEntity.Type toEntity(Wallet.Type type) {
        return type == null ? null : WalletEntity.Type.valueOf(type.name());
    }
}
