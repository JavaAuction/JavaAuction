package com.javaauction.payment_service.infrastructure.persistence.repository;

import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.repository.WalletRepository;
import com.javaauction.payment_service.infrastructure.persistence.entity.WalletEntity;
import com.javaauction.payment_service.infrastructure.persistence.mapper.WalletMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletRepositoryImpl implements WalletRepository {

    private final WalletJpaRepository walletJpaRepository;
    private final WalletMapper walletMapper;

    @Override
    @Transactional
    public Wallet save(Wallet wallet) {
        WalletEntity entity;

        if (wallet.getId() != null) {
            entity = walletJpaRepository.findById(wallet.getId())
                    .orElseGet(() -> walletMapper.toEntity(wallet));
        } else {
            entity = walletMapper.toEntity(wallet);
        }

        WalletEntity saved = walletJpaRepository.save(entity);
        return walletMapper.toDomain(saved);
    }

    @Override
    public Optional<Wallet> findById(UUID walletId) {
        return walletJpaRepository.findById(walletId)
                .map(walletMapper::toDomain);
    }
}
