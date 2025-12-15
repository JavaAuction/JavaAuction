package com.javaauction.payment_service.infrastructure.persistence.repository;

import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.repository.WalletRepository;
import com.javaauction.payment_service.infrastructure.persistence.entity.QWalletEntity;
import com.javaauction.payment_service.infrastructure.persistence.entity.WalletEntity;
import com.javaauction.payment_service.infrastructure.persistence.mapper.WalletMapper;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.javaauction.payment_service.presentation.advice.PaymentErrorCode.WALLET_ALREADY_DELETED;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletRepositoryImpl implements WalletRepository {

    private final WalletJpaRepository walletJpaRepository;
    private final WalletMapper walletMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWalletEntity wallet = QWalletEntity.walletEntity;

    @Override
    @Transactional
    public Wallet save(Wallet wallet) {
        WalletEntity entity = walletMapper.toEntity(wallet);
        WalletEntity saved = walletJpaRepository.save(entity);

        return walletMapper.toDomain(saved);
    }

    @Override
    public Optional<Wallet> findById(UUID walletId) {
        return walletJpaRepository.findById(walletId)
                .map(walletMapper::toDomain);
    }

    @Override
    public Optional<Wallet> findByUserId(String userId) {
        return walletJpaRepository.findByUserId(userId)
                .map(walletMapper::toDomain);
    }

    @Override
    public void delete(UUID walletId) {
        long delete = queryFactory
                .update(wallet)
                .set(wallet.deletedAt, Instant.now())
                .set(wallet.deletedBy, "system")
                .where(
                        wallet.id.eq(walletId),
                        wallet.deletedAt.isNull()
                )
                .execute();

        if (delete == 0)
            throw new PaymentException(WALLET_ALREADY_DELETED);
    }
}
