package com.javaauction.payment_service.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.payment_service.application.service.WalletTransactionServiceV1;
import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.presentation.dto.response.ResGetTransactionDto;
import com.javaauction.payment_service.presentation.dto.response.ResGetTransactionsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.javaauction.payment_service.presentation.advice.PaymentSuccessCode.WALLET_TRANSACTION_READ_SUCCESS;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/wallets/{walletId}/transactions")
public class WalletTransactionControllerV1 {

    private final WalletTransactionServiceV1 walletTransactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ResGetTransactionsDto>>> getTransactions(
            @PathVariable UUID walletId,
            @PageableDefault(page = 0, size = 15, sort = "createdAt", direction = DESC) Pageable pageable,
            @RequestParam(required = false) List<TransactionType> transactionTypes,
            @RequestParam(required = false) Long minAmount,
            @RequestParam(required = false) Long maxAmount
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_TRANSACTION_READ_SUCCESS,
                        walletTransactionService.getTransactions(walletId, pageable, transactionTypes, minAmount, maxAmount)
                )
        );
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<ResGetTransactionDto>> getTransaction(
            @PathVariable UUID walletId, @PathVariable UUID transactionId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_TRANSACTION_READ_SUCCESS,
                        walletTransactionService.getTransaction(walletId, transactionId)
                )
        );
    }
}
