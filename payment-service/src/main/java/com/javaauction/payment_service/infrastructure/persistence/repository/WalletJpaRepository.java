package com.javaauction.payment_service.infrastructure.persistence.repository;

import com.javaauction.payment_service.infrastructure.persistence.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletJpaRepository extends JpaRepository<WalletEntity, UUID> {
    Optional<WalletEntity> findByUserId(String userId);
}
