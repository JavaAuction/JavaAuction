package com.javaauction.payment_service.infrastructure.persistence.repository;

import com.javaauction.payment_service.domain.model.WalletTransaction;
import com.javaauction.payment_service.domain.repository.WalletTransactionRepository;
import com.javaauction.payment_service.infrastructure.persistence.entity.WalletEntity;
import com.javaauction.payment_service.infrastructure.persistence.entity.WalletTransactionEntity;
import com.javaauction.payment_service.infrastructure.persistence.mapper.WalletTransactionMapper;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.javaauction.payment_service.presentation.advice.PaymentErrorCode.WALLET_NOT_FOUND;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletTransactionRepositoryImpl implements WalletTransactionRepository {

    private final WalletTransactionJpaRepository walletTransactionJpaRepository;
    private final WalletJpaRepository walletJpaRepository;
    private final WalletTransactionMapper walletTransactionMapper;

    @Override
    @Transactional
    public WalletTransaction save(WalletTransaction walletTransaction) {

        WalletEntity walletEntity = walletJpaRepository.findById(walletTransaction.getWalletId())
                .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));

        WalletTransactionEntity entity = walletTransactionMapper.toEntity(walletTransaction, walletEntity);
        WalletTransactionEntity saved = walletTransactionJpaRepository.save(entity);

        return walletTransactionMapper.toDomain(saved);
    }

    @Override
    public List<WalletTransaction> findByWalletId(UUID walletId) {
        return walletTransactionJpaRepository.findByWallet_Id(walletId).stream()
                .map(walletTransactionMapper::toDomain)
                .toList();
    }
}
