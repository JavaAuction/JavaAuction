package com.javaauction.payment_service.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.payment_service.application.service.WalletTransactionServiceV1;
import com.javaauction.payment_service.presentation.dto.request.ReqSettleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.javaauction.payment_service.presentation.advice.PaymentSuccessCode.WALLET_TRANSACTION_SETTLE_SUCCESS;

@Tag(name = "Internal Wallet Transaction", description = "거래내역 내부 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/wallets/transactions")
public class InternalWalletTransactionControllerV1 {

    private final WalletTransactionServiceV1 walletTransactionService;

    @Operation(summary = "경매 정산", description = "경매에서 즉시 구매가 발생하거나 낙찰이 확정되었을 때, 해당 거래 금액을 정산합니다.")
    @PostMapping("/settlement")
    public ResponseEntity<ApiResponse<Void>> settle(
            @Parameter(description = "경매 정산 요청 DTO") @Valid @RequestBody ReqSettleDto request
    ) {
        walletTransactionService.settle(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(WALLET_TRANSACTION_SETTLE_SUCCESS)
        );
    }
}
