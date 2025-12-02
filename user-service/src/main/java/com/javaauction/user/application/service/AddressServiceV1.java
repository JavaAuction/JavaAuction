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
    public void createAddress(String userId, ReqCreateAddressDto dto, String username) {
        UserEntity user = getUserOrThrow(userId);
        validateOwnership(user, username);

        boolean isDuplicate = checkDuplicateAddress(user, dto.getAddress(), dto.getPostcode(), dto.getAddressDetail(), null);

        if (isDuplicate) {
            throw new BussinessException(UserErrorCode.ADDRESS_ALREADY_EXISTS);
        }

        AddressEntity currentDefault = getCurrentDefaultAddress(user);

        AddressEntity address = AddressEntity.builder()
                .address(dto.getAddress())
                .postcode(dto.getPostcode())
                .detail(dto.getAddressDetail())
                .user(user)
                .isDefault(currentDefault == null || dto.isDefault())
                .build();

        address.setCreate(Instant.now(), username);

        // 기존 default 해제
        if (currentDefault != null && dto.isDefault()) {
            currentDefault.changeDefaultAddress(false);
        }

        addressRepository.save(address);

        if (address.isDefault()) {
            user.setAddress(address.getAddressId());
            user.setUpdated(Instant.now(), username);
        }
    }

    @Transactional(readOnly = true)
    public Object getAddress(int page, int size, String sortBy, boolean isAsc, String role, String username) {
        if ("ADMIN".equals(role)) {
            if (!List.of(10, 30, 50).contains(size)) size = 10;
            if (sortBy == null || sortBy.isBlank() || !sortBy.equals("modifiedAt"))
                sortBy = "createdAt";

            Pageable pageable = PageRequest.of(page > 0 ? page - 1 : page,
                    size,
                    isAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
                    sortBy);

            Page<AddressEntity> addressList = addressRepository.findAll(pageable);
            return addressList.map(ResGetAddressDto::of);
        }

        UserEntity user = getUserOrThrow(username);
        return addressRepository.findByUser(user)
                .stream()
                .map(ResGetAddressDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateAddress(UUID addressId, ReqUpdateAddressDto req, String username) {
        AddressEntity address = getAddressOrThrow(addressId);
        UserEntity user = getUserOrThrow(username);

        validateOwnership(address.getUser(), username);

        boolean isDuplicate = checkDuplicateAddress(
                user, req.getAddress(), req.getPostcode(), req.getAddressDetail(), addressId
        );

        if (isDuplicate) {
            throw new BussinessException(UserErrorCode.ADDRESS_ALREADY_EXISTS);
        }

        AddressEntity currentDefault = getCurrentDefaultAddress(user);

        // 기본 주소인데 기본=false로 변경하려고 하면 불가
        if (!req.isDefault() && currentDefault != null &&
                currentDefault.getAddressId().equals(addressId)) {
            throw new BussinessException(UserErrorCode.ADDRESS_DEFAULT_DISAPPEAR);
        }

        // 새로운 default라면 기존 default 해제
        if (req.isDefault()) {
            if (currentDefault != null && !currentDefault.getAddressId().equals(addressId)) {
                currentDefault.changeDefaultAddress(false);
            }
            user.setAddress(addressId);
        }

        address.update(req);
        address.setUpdated(Instant.now(), username);
    }

    @Transactional
    public void deleteAddress(UUID addressId, String username) {
        AddressEntity address = getAddressOrThrow(addressId);

        validateOwnership(address.getUser(), username);

        if (address.isDefault()) {
            throw new BussinessException(UserErrorCode.ADDRESS_DEFAULT_DISAPPEAR);
        }

        address.softDelete(Instant.now(), username);
    }

    private UserEntity getUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));
    }

    private AddressEntity getAddressOrThrow(UUID addressId) {
        return addressRepository.findByAddressId(addressId)
                .orElseThrow(() -> new BussinessException(UserErrorCode.ADDRESS_NOT_FOUND));
    }

    private void validateOwnership(UserEntity user, String username) {
        if (!user.getUsername().equals(username)) {
            throw new BussinessException(BaseErrorCode.ACCESS_DENIED);
        }
    }

    private AddressEntity getCurrentDefaultAddress(UserEntity user) {
        return addressRepository.findByUserAndIsDefault(user, true);
    }

    private boolean checkDuplicateAddress(
            UserEntity user,
            String address,
            String postcode,
            String detail,
            UUID exceptId
    ) {
        List<AddressEntity> addresses = addressRepository.findByUser(user);

        return addresses.stream()
                .filter(a -> exceptId == null || !a.getAddressId().equals(exceptId))
                .anyMatch(a ->
                        a.getAddress().equals(address) &&
                                a.getPostcode().equals(postcode) &&
                                a.getDetail().equals(detail)
                );
    }
}


