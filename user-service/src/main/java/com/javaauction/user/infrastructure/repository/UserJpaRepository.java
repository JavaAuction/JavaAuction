package com.javaauction.user.infrastructure.repository;

import com.javaauction.user.domain.entity.UserEntity;
import jdk.jfr.Registered;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Registered
public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findBySlackId(String slackId);
    Page<UserEntity> findAllByDeletedAtIsNull(Pageable pageable);
}
