package com.javaauction.payment_service.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.payment_service.application.service.WalletServiceV1;
import com.javaauction.payment_service.presentation.dto.request.ReqCreateWalletDto;
import com.javaauction.payment_service.presentation.dto.request.ReqHoldDto;
import com.javaauction.payment_service.presentation.dto.request.ReqPaymentDto;
import com.javaauction.payment_service.presentation.dto.response.ResCreateWalletDto;
import com.javaauction.payment_service.presentation.dto.response.ResHoldDto;
import com.javaauction.payment_service.presentation.dto.response.ResPaymentDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.javaauction.payment_service.presentation.advice.PaymentSuccessCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/wallets")
public class InternalWalletControllerV1 {

    private final WalletServiceV1 walletService;

    @PostMapping
    public ResponseEntity<ApiResponse<ResCreateWalletDto>> createWallet(@Valid @RequestBody ReqCreateWalletDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        WALLET_CREATE_SUCCESS,
                        walletService.createWallet(request)
                )
        );
    }

    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<ResPaymentDto>> payment(@Valid @RequestBody ReqPaymentDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        WALLET_PAYMENT_SUCCESS,
                        walletService.payment(request)
                )
        );
    }

    @PostMapping("/holds")
    public ResponseEntity<ApiResponse<ResHoldDto>> deduct(@Valid @RequestBody ReqHoldDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        WALLET_HOLD_SUCCESS,
                        walletService.hold(request)
                )
        );
    }
}
