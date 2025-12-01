package com.javaauction.alertservice.infrastructure.repository;

import com.javaauction.alertservice.domain.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlertJpaRepository extends JpaRepository<Alert, UUID>, AlertRepository{
    // 알림 존재 여부 확인
    Optional<Alert> findByAlertIdAndDeletedAtIsNull(UUID alertId);
}
