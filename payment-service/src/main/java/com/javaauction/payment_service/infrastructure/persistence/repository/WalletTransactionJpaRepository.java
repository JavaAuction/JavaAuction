package com.javaauction.payment_service.infrastructure.persistence.repository;

import com.javaauction.payment_service.domain.enums.HoldStatus;
import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.infrastructure.persistence.entity.WalletTransactionEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface WalletTransactionJpaRepository extends JpaRepository<WalletTransactionEntity, UUID> {

    Optional<WalletTransactionEntity> findByAuctionIdAndTransactionType(UUID auctionId, TransactionType transactionType);

    Optional<WalletTransactionEntity> findByAuctionIdAndTransactionTypeAndHoldStatus(
            UUID auctionId, TransactionType transactionType, HoldStatus holdStatus
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                select wt from WalletTransactionEntity wt
                where wt.auctionId = :auctionId
                  and wt.transactionType = com.javaauction.payment_service.domain.enums.TransactionType.HOLD
                  and wt.holdStatus = com.javaauction.payment_service.domain.enums.HoldStatus.HOLD_ACTIVE
            """)
    Optional<WalletTransactionEntity> findActiveHoldForUpdate(@Param("auctionId") UUID auctionId);
}
