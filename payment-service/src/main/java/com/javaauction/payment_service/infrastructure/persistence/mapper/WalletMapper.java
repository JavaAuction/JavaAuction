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
                .balance(entity.getBalance())
                .build();
    }

    public WalletEntity toEntity(Wallet wallet) {
        if (wallet == null) return null;

        return WalletEntity.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .build();
    }
}
