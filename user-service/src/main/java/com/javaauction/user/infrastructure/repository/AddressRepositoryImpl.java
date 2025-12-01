package com.javaauction.user.infrastructure.repository;

import com.javaauction.user.domain.entity.AddressEntity;
import com.javaauction.user.domain.entity.UserEntity;
import com.javaauction.user.domain.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AddressRepositoryImpl implements AddressRepository {

    private final AddressJpaRepository addressJpaRepository;

    @Override
    public List<AddressEntity> findByUser(UserEntity user) {
        return addressJpaRepository.findByUser(user);
    }

    @Override
    public Page<AddressEntity> findAll(Pageable pageable) {
        return addressJpaRepository.findAll(pageable);
    }

    @Override
    public AddressEntity findByUserAndIsDefault(UserEntity user, boolean isDefault) {
        return addressJpaRepository.findByUserAndIsDefault(user, isDefault);
    }

    @Override
    public AddressEntity save(AddressEntity address) {
        return addressJpaRepository.save(address);
    }
}
