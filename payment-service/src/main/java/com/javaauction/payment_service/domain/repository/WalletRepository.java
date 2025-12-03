package com.javaauction.payment_service.domain.repository;

import com.javaauction.payment_service.domain.model.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {

    Wallet save(Wallet wallet);

    Optional<Wallet> findById(UUID walletId);

    Optional<Wallet> findWalletByUserId(String userId);
}
