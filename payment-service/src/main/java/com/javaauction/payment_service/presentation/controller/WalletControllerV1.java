package com.javaauction.payment_service.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.payment_service.application.service.WalletServiceV1;
import com.javaauction.payment_service.presentation.dto.request.ReqChargeDto;
import com.javaauction.payment_service.presentation.dto.request.ReqWithdrawDto;
import com.javaauction.payment_service.presentation.dto.response.ResChargeDto;
import com.javaauction.payment_service.presentation.dto.response.ResGetWallet;
import com.javaauction.payment_service.presentation.dto.response.ResWithdrawDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.javaauction.payment_service.presentation.advice.PaymentSuccessCode.*;
import static com.javaauction.payment_service.presentation.constant.HttpHeaderNames.ROLE;
import static com.javaauction.payment_service.presentation.constant.HttpHeaderNames.USERNAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/wallets")
public class WalletControllerV1 {

    private final WalletServiceV1 walletService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ResGetWallet>> getWalletByUserId(@RequestHeader(USERNAME) String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_READ_SUCCESS,
                        walletService.getWalletByUserId(userId)
                )
        );
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<ApiResponse<ResGetWallet>> getWalletById(
            @PathVariable UUID walletId, @RequestHeader(ROLE) String role
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_READ_SUCCESS,
                        walletService.getWalletById(walletId, role)
                )
        );
    }

    @PostMapping("/{walletId}/charge")
    public ResponseEntity<ApiResponse<ResChargeDto>> charge(
            @PathVariable UUID walletId, @Valid @RequestBody ReqChargeDto request,
            @RequestHeader(USERNAME) String userId, @RequestHeader(ROLE) String role
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_CHARGE_SUCCESS,
                        walletService.charge(walletId, request, userId, role)
                )
        );
    }

    @PostMapping("/{walletId}/withdrawal")
    public ResponseEntity<ApiResponse<ResWithdrawDto>> withdraw(
            @PathVariable UUID walletId, @Valid @RequestBody ReqWithdrawDto request,
            @RequestHeader(USERNAME) String userId, @RequestHeader(ROLE) String role
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_WITHDRAW_SUCCESS,
                        walletService.withdraw(walletId, request, userId, role)
                )
        );
    }
}
