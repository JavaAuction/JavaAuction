package com.javaauction.payment_service.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.payment_service.application.service.WalletTransactionServiceV1;
import com.javaauction.payment_service.presentation.dto.request.ReqReleaseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.javaauction.payment_service.presentation.advice.PaymentSuccessCode.WALLET_TRANSACTION_SETTLEMENT_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/wallets/transactions")
public class InternalWalletTransactionControllerV1 {

    private final WalletTransactionServiceV1 walletTransactionService;

    @PostMapping("/settlement")
    public ResponseEntity<ApiResponse<?>> settleAuction(@Valid @RequestBody ReqReleaseDto request) {
        walletTransactionService.settleAuction(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_TRANSACTION_SETTLEMENT_SUCCESS
                )
        );
    }
}
