package com.javaauction.user.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.user.application.dto.ReqCreateAddressDto;
import com.javaauction.user.application.service.AddressServiceV1;
import com.javaauction.user.infrastructure.JWT.JwtUserContext;
import com.javaauction.user.presentation.advice.UserSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users/{userId}/address")
@RequiredArgsConstructor
public class AddressControllerV1 {

    private final AddressServiceV1 addressService;

    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createAddress(
            @PathVariable String userId,
            @RequestBody ReqCreateAddressDto req) {
        addressService.createAddress(userId, req, JwtUserContext.getUsernameFromHeader());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(UserSuccessCode.ADDRESS_CREATED));
    }
}

