package com.javaauction.payment_service.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.payment_service.application.service.WalletTransactionServiceV1;
import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.presentation.dto.response.ResGetTransactionDto;
import com.javaauction.payment_service.presentation.dto.response.ResGetTransactionsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Wallet Transaction", description = "거래내역 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/wallets/{walletId}/transactions")
public class WalletTransactionControllerV1 {

    private final WalletTransactionServiceV1 walletTransactionService;

    @Operation(summary = "거래내역 조회",
            description = """
                    해당 지갑에서 일치하는 조건의 모든 거래내역을 조회합니다.
                    
                    - page: 페이지 번호 (default 0)
                    - size: 페이지 크기 (default 15)
                    - sort: 정렬 기준 (default createdAt,DESC)
                    """
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ResGetTransactionsDto>>> getTransactions(
            @Parameter(description = "지갑 ID") @PathVariable UUID walletId,
            @Parameter(hidden = true) @PageableDefault(page = 0, size = 15, sort = "createdAt", direction = DESC) Pageable pageable,
            @Parameter(description = "거래 유형") @RequestParam(required = false) List<TransactionType> transactionTypes,
            @Parameter(description = "최소 금액") @RequestParam(required = false) Long minAmount,
            @Parameter(description = "최대 금액") @RequestParam(required = false) Long maxAmount
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_TRANSACTION_READ_SUCCESS,
                        walletTransactionService.getTransactions(walletId, pageable, transactionTypes, minAmount, maxAmount)
                )
        );
    }

    @Operation(summary = "특정 거래내역 조회", description = "특정 거래내역의 정보를 조회합니다.")
    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<ResGetTransactionDto>> getTransaction(
            @Parameter(description = "지갑 ID") @PathVariable UUID walletId,
            @Parameter(description = "거래내역 ID") @PathVariable UUID transactionId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_TRANSACTION_READ_SUCCESS,
                        walletTransactionService.getTransaction(walletId, transactionId)
                )
        );
    }
}
