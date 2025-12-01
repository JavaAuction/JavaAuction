package com.javaauction.user.infrastructure.repository;

import com.javaauction.user.domain.entity.AddressEntity;
import com.javaauction.user.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddressJpaRepository extends JpaRepository<AddressEntity, UUID> {
    List<AddressEntity> findByUser(UserEntity user);

    AddressEntity findByUserAndIsDefault(UserEntity user, boolean isDefault);

    Page<AddressEntity> findAll(Pageable pageable);
}