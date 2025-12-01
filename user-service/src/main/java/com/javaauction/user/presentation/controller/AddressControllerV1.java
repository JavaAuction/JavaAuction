package com.javaauction.user.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.user.application.dto.ReqCreateAddressDto;
import com.javaauction.user.application.service.AddressServiceV1;
import com.javaauction.user.domain.entity.AddressEntity;
import com.javaauction.user.infrastructure.JWT.JwtUserContext;
import com.javaauction.user.presentation.advice.UserSuccessCode;
import com.javaauction.user.presentation.dto.ResGetAddressDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/address")
@RequiredArgsConstructor
public class AddressControllerV1 {

    private final AddressServiceV1 addressService;

    @PostMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> createAddress(
            @PathVariable String userId,
            @RequestBody ReqCreateAddressDto req) {
        addressService.createAddress(userId, req, JwtUserContext.getUsernameFromHeader());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(UserSuccessCode.ADDRESS_CREATED));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Object>> getAllAddress(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {

        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.ADDRESS_LIST_FOUND, addressService.getAddress(page - 1, size, sortBy, isAsc, JwtUserContext.getRoleFromHeader(), JwtUserContext.getUsernameFromHeader())));
    }
}

