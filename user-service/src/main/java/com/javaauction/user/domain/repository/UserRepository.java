package com.javaauction.user.domain.repository;

import com.javaauction.user.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    Page<UserEntity> getUsers(Pageable pageable);
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findBySlackId(String slackId);
    UserEntity save(UserEntity user);
}
