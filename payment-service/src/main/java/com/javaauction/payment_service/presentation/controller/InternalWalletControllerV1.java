package com.javaauction.payment_service.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.payment_service.application.service.WalletServiceV1;
import com.javaauction.payment_service.presentation.dto.request.ReqCreateWalletDto;
import com.javaauction.payment_service.presentation.dto.request.ReqDeductDto;
import com.javaauction.payment_service.presentation.dto.request.ReqValidateDto;
import com.javaauction.payment_service.presentation.dto.response.ResCreateWalletDto;
import com.javaauction.payment_service.presentation.dto.response.ResDeductDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.javaauction.payment_service.presentation.advice.PaymentErrorCode.WALLET_INSUFFICIENT_BALANCE;
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

    @PostMapping("/deductions")
    public ResponseEntity<ApiResponse<ResDeductDto>> deduct(@Valid @RequestBody ReqDeductDto request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_DEDUCT_SUCCESS,
                        walletService.deduct(request)
                )
        );
    }

    @PostMapping("/validations")
    public ResponseEntity<ApiResponse<?>> validate(@Valid @RequestBody ReqValidateDto request) {

        return walletService.validate(request)
                ? ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(WALLET_VALIDATE_SUCCESS))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(WALLET_INSUFFICIENT_BALANCE));
    }
}
