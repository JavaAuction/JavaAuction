package com.javaauction.user.domain.repository;

import com.javaauction.user.domain.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    Optional<UserEntity> findByUsername(String email);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findBySlackId(String slackId);
    UserEntity save(UserEntity user);
}
