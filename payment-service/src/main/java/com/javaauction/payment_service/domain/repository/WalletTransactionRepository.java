package com.javaauction.payment_service.domain.repository;

import com.javaauction.payment_service.domain.model.WalletTransaction;

import java.util.List;
import java.util.UUID;

public interface WalletTransactionRepository {

    WalletTransaction save(WalletTransaction transaction);

    List<WalletTransaction> findByWalletId(UUID walletId);
}
