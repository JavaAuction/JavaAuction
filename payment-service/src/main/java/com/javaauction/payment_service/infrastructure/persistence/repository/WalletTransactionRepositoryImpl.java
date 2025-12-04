package com.javaauction.payment_service.infrastructure.persistence.repository;

import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import com.javaauction.payment_service.domain.repository.WalletTransactionRepository;
import com.javaauction.payment_service.infrastructure.persistence.entity.QWalletTransactionEntity;
import com.javaauction.payment_service.infrastructure.persistence.entity.WalletEntity;
import com.javaauction.payment_service.infrastructure.persistence.entity.WalletTransactionEntity;
import com.javaauction.payment_service.infrastructure.persistence.mapper.WalletTransactionMapper;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.javaauction.payment_service.presentation.advice.PaymentErrorCode.WALLET_NOT_FOUND;
import static com.javaauction.payment_service.presentation.advice.PaymentErrorCode.WALLET_TRANSACTION_NOT_FOUND;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletTransactionRepositoryImpl implements WalletTransactionRepository {

    private final WalletTransactionJpaRepository walletTransactionJpaRepository;
    private final WalletJpaRepository walletJpaRepository;
    private final WalletTransactionMapper walletTransactionMapper;
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

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
    public Page<WalletTransaction> findByWalletId(
            UUID walletId, Pageable pageable, List<TransactionType> transactionTypes, Long minAmount, Long maxAmount
    ) {
        QWalletTransactionEntity walletTransaction = QWalletTransactionEntity.walletTransactionEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(walletTransaction.wallet.id.eq(walletId));

        if (transactionTypes != null && !transactionTypes.isEmpty()) {
            builder.and(walletTransaction.transactionType.in(transactionTypes));
        }
        if (minAmount != null) {
            builder.and(walletTransaction.amount.goe(minAmount));
        }
        if (maxAmount != null) {
            builder.and(walletTransaction.amount.loe(maxAmount));
        }

        JPQLQuery<WalletTransactionEntity> baseQuery = queryFactory
                .selectFrom(walletTransaction)
                .where(builder);

        Querydsl querydsl = new Querydsl(
                entityManager,
                new PathBuilder<>(WalletTransactionEntity.class, walletTransaction.getMetadata())
        );

        JPQLQuery<WalletTransactionEntity> pagingQuery =
                querydsl.applyPagination(pageable, baseQuery);

        List<WalletTransaction> content = baseQuery.fetch().stream()
                .map(walletTransactionMapper::toDomain)
                .toList();

        Long count = queryFactory
                .select(walletTransaction.count())
                .from(walletTransaction)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, count != null ? count : 0L);
    }

    @Override
    public WalletTransaction findById(UUID transactionId) {
        WalletTransactionEntity walletTransactionEntity = walletTransactionJpaRepository.findById(transactionId)
                .orElseThrow(() -> new PaymentException(WALLET_TRANSACTION_NOT_FOUND));

        return walletTransactionMapper.toDomain(walletTransactionEntity);
    }
}
