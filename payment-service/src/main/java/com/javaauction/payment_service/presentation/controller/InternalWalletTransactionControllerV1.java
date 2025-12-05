package com.javaauction.payment_service.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.payment_service.application.service.WalletTransactionServiceV1;
import com.javaauction.payment_service.presentation.dto.request.ReqCaptureDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.javaauction.payment_service.presentation.advice.PaymentSuccessCode.WALLET_TRANSACTION_HOLD_CAPTURED_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/wallets/transactions")
public class InternalWalletTransactionControllerV1 {

    private final WalletTransactionServiceV1 walletTransactionService;

    @PostMapping("/capture")
    public ResponseEntity<ApiResponse<Void>> capture(@Valid @RequestBody ReqCaptureDto request) {
        walletTransactionService.capture(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(WALLET_TRANSACTION_HOLD_CAPTURED_SUCCESS)
        );
    }
}
