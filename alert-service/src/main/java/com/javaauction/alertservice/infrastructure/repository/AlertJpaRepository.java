package com.javaauction.alertservice.infrastructure.repository;

import com.javaauction.alertservice.domain.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlertJpaRepository extends JpaRepository<Alert, UUID>, AlertRepository{

}
