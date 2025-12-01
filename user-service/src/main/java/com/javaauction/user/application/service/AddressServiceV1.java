package com.javaauction.user.application.service;

import com.javaauction.global.infrastructure.code.BaseErrorCode;
import com.javaauction.global.presentation.exception.BussinessException;
import com.javaauction.user.application.dto.ReqCreateAddressDto;
import com.javaauction.user.domain.entity.AddressEntity;
import com.javaauction.user.domain.entity.UserEntity;
import com.javaauction.user.domain.repository.AddressRepository;
import com.javaauction.user.domain.repository.UserRepository;
import com.javaauction.user.presentation.advice.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AddressServiceV1 {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createAddress(String userId, ReqCreateAddressDto createAddressDto, String username) {
        UserEntity user = userRepository.findByUsername(userId).orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));
        //본인확인
        if(!user.getUsername().equals(username)) {
            throw new BussinessException(BaseErrorCode.ACCESS_DENIED);
        }

        AddressEntity defaultAddress = addressRepository.findByUserAndIsDefault(user, true);


        AddressEntity addressEntity = AddressEntity.builder()
                .address(createAddressDto.getAddress())
                .user(user)
                .postcode(createAddressDto.getPostcode())
                .detail(createAddressDto.getAddressDetail())
                .isDefault(defaultAddress != null ? createAddressDto.isDefault() : true)
                .build();

        addressEntity.setCreate(Instant.now(),username);

        if(defaultAddress != null && createAddressDto.isDefault()) {
            defaultAddress.changeDefaultAddress(false);
        }

        addressRepository.save(addressEntity);

        if(addressEntity.isDefault()) {
            user.setAddress(addressEntity.getAddressId());
            user.setUpdated(Instant.now(),username);
        }
    }
}

