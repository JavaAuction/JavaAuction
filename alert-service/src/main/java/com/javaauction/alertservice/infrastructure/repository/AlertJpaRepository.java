package com.javaauction.alertservice.infrastructure.repository;

import com.javaauction.alertservice.domain.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertJpaRepository extends JpaRepository<Alert, UUID>, AlertRepository{
    // 알림 존재 여부 확인
    Optional<Alert> findByAlertIdAndDeletedAtIsNull(UUID alertId);

    // 알림 ID 리스트의 존재 여부 (알림 선택 삭제용)
    List<Alert> findAllByAlertIdInAndDeletedAtIsNull(List<UUID> ids);
}
