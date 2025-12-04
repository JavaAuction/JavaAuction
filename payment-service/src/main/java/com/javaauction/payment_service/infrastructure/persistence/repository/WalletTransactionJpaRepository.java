package com.javaauction.payment_service.infrastructure.persistence.repository;

import com.javaauction.payment_service.domain.enums.HoldStatus;
import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.infrastructure.persistence.entity.WalletTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WalletTransactionJpaRepository extends JpaRepository<WalletTransactionEntity, UUID> {

    List<WalletTransactionEntity> findByAuctionIdAndTransactionTypeAndHoldStatus(
            UUID auctionId, TransactionType transactionType, HoldStatus holdStatus
    );
}
