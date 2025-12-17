package com.javaauction.user.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.user.application.dto.ReqCreateAddressDto;
import com.javaauction.user.application.dto.ReqUpdateAddressDto;
import com.javaauction.user.application.service.AddressServiceV1;
import com.javaauction.user.domain.entity.AddressEntity;
import com.javaauction.user.infrastructure.JWT.JwtUserContext;
import com.javaauction.user.presentation.advice.UserSuccessCode;
import com.javaauction.user.presentation.dto.ResGetAddressDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/address")
@RequiredArgsConstructor
@Tag(name = "사용자 주소 API", description = "사용자 주소 관련 기능 API입니다.")
public class AddressControllerV1 {

    private final AddressServiceV1 addressService;

    @Operation(summary = "주소 생성", description = "입력 정보로 주소를 생성")
    @PostMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> createAddress(
            @PathVariable String userId,
            @RequestBody ReqCreateAddressDto req) {
        addressService.createAddress(userId, req, JwtUserContext.getUsernameFromHeader());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(UserSuccessCode.ADDRESS_CREATED));
    }

    @Operation(summary = "주소 목록 조회", description = "주소 목록을 조회(관리자만 가능)")
    @GetMapping()
    public ResponseEntity<ApiResponse<Object>> getAllAddress(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {

        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.ADDRESS_LIST_FOUND, addressService.getAddress(page - 1, size, sortBy, isAsc, JwtUserContext.getRoleFromHeader(), JwtUserContext.getUsernameFromHeader())));
    }

    @Operation(summary = "주소 수정", description = "입력 정보로 주소를 수정(수정할 값만 입력)")
    @PostMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> updateAddress(@PathVariable UUID addressId, @RequestBody ReqUpdateAddressDto req) {
        addressService.updateAddress(addressId, req, JwtUserContext.getUsernameFromHeader());
        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.ADDRESS_UPDATED));
    }

    @Operation(summary = "주소 삭제", description = "해당 주소를 삭제")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable UUID addressId) {
        addressService.deleteAddress(addressId, JwtUserContext.getUsernameFromHeader());
        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.ADDRESS_DELETED));
    }
}

