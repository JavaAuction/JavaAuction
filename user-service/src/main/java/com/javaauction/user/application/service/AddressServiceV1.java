package com.javaauction.user.application.service;

import com.javaauction.global.infrastructure.code.BaseErrorCode;
import com.javaauction.global.presentation.exception.BussinessException;
import com.javaauction.user.application.dto.ReqCreateAddressDto;
import com.javaauction.user.application.dto.ReqUpdateAddressDto;
import com.javaauction.user.domain.entity.AddressEntity;
import com.javaauction.user.domain.entity.UserEntity;
import com.javaauction.user.domain.repository.AddressRepository;
import com.javaauction.user.domain.repository.UserRepository;
import com.javaauction.user.presentation.advice.UserErrorCode;
import com.javaauction.user.presentation.advice.UserException;
import com.javaauction.user.presentation.dto.ResGetAddressDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

        //중복 확인
        List<AddressEntity> existingAddresses = addressRepository.findByUser(user);
        boolean isDuplicate = existingAddresses.stream()
                .anyMatch(existing ->
                        existing.getAddress().equals(createAddressDto.getAddress()) &&
                                existing.getPostcode().equals(createAddressDto.getPostcode()) &&
                                existing.getDetail().equals(createAddressDto.getAddressDetail())
                );

        if (isDuplicate) {
            throw new BussinessException(UserErrorCode.ADDRESS_ALREADY_EXISTS);
        }

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

    @Transactional(readOnly = true)
    public Object getAddress(int page, int size, String sortBy, boolean isAsc, String role, String username) {
        if ("ADMIN".equals(role)) {
            // ADMIN - 전체 조회
            if (!List.of(10, 30, 50).contains(size)) size = 10;
            if (sortBy == null || sortBy.isBlank() || !sortBy.equals("modifiedAt")) sortBy = "createdAt";

            Pageable pageable = PageRequest.of(page > 0 ? page - 1 : page, size, isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
            Page<AddressEntity> addressList = addressRepository.findAll(pageable);
            return addressList.map(ResGetAddressDto::of);
        } else {
            // USER - 본인 주소 조회
            UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));
            return addressRepository.findByUser(user).stream()
                    .map(ResGetAddressDto::of)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public void updateAddress(UUID addressId, ReqUpdateAddressDto req, String username) {
        AddressEntity address = addressRepository.findByAddressId(addressId).orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if(!address.getUser().getUsername().equals(username)) {
            throw new BussinessException(BaseErrorCode.ACCESS_DENIED);
        }

        List<AddressEntity> existingAddresses = addressRepository.findByUser(user);
        boolean isDuplicate = existingAddresses.stream()
                .anyMatch(existing ->
                        existing.getAddress().equals(req.getAddress()) &&
                                existing.getPostcode().equals(req.getPostcode()) &&
                                existing.getDetail().equals(req.getAddressDetail())
                );

        if (isDuplicate) {
            throw new BussinessException(UserErrorCode.ADDRESS_ALREADY_EXISTS);
        }

        if(req.isDefault()== true){
            addressRepository.findByUserAndIsDefault(user, true).changeDefaultAddress(false);
        }

        address.update(req);
        address.setUpdated(Instant.now(),username);
    }
}

