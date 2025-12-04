package com.javaauction.payment_service.domain.repository;

import com.javaauction.payment_service.domain.enums.HoldStatus;
import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletTransactionRepository {

    WalletTransaction save(WalletTransaction transaction);

    Page<WalletTransaction> findByWalletId(
            UUID walletId, Pageable pageable, List<TransactionType> transactionTypes, Long minAmount, Long maxAmount
    );

    WalletTransaction findById(UUID transactionId);

    Optional<WalletTransaction> findByAuctionIdAndTransactionTypeAndHoldStatus(
            UUID auctionId, TransactionType transactionType, HoldStatus holdStatus
    );
}
