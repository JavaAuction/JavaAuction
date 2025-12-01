package com.javaauction.user.domain.repository;

import com.javaauction.user.domain.entity.AddressEntity;
import com.javaauction.user.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository {
    List<AddressEntity> findByUser(UserEntity user);
    Page<AddressEntity> findAll(Pageable pageable);
    AddressEntity findByUserAndIsDefault(UserEntity user, boolean isDefault);
    AddressEntity save(AddressEntity address);
    Optional<AddressEntity> findByAddressId(UUID addressId);
}
