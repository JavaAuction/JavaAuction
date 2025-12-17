package com.javaauction.payment_service.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.payment_service.application.service.WalletServiceV1;
import com.javaauction.payment_service.presentation.dto.request.ReqChargeDto;
import com.javaauction.payment_service.presentation.dto.request.ReqWithdrawDto;
import com.javaauction.payment_service.presentation.dto.response.ResChargeDto;
import com.javaauction.payment_service.presentation.dto.response.ResGetWallet;
import com.javaauction.payment_service.presentation.dto.response.ResWithdrawDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.javaauction.payment_service.presentation.advice.PaymentSuccessCode.*;
import static com.javaauction.payment_service.presentation.constant.HttpHeaderNames.ROLE;
import static com.javaauction.payment_service.presentation.constant.HttpHeaderNames.USERNAME;

@Tag(name = "Wallet", description = "지갑 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/wallets")
public class WalletControllerV1 {

    private final WalletServiceV1 walletService;

    @Operation(summary = "내 지갑 조회", description = "현재 로그인한 사용자를 기반으로 지갑 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ResGetWallet>> getWalletByUserId(
            @Parameter(hidden = true) @RequestHeader(USERNAME) String userId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_READ_SUCCESS,
                        walletService.getWalletByUserId(userId)
                )
        );
    }

    @Hidden
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

    @Operation(summary = "현금 충전", description = "사용자의 지갑에 현금을 충전합니다.")
    @PostMapping("/{walletId}/charge")
    public ResponseEntity<ApiResponse<ResChargeDto>> charge(
            @Parameter(description = "지갑 ID") @PathVariable UUID walletId,
            @Parameter(description = "현금 충전 요청 DTO") @Valid @RequestBody ReqChargeDto request,
            @Parameter(hidden = true) @RequestHeader(USERNAME) String userId,
            @Parameter(hidden = true) @RequestHeader(ROLE) String role
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_CHARGE_SUCCESS,
                        walletService.charge(walletId, request, userId, role)
                )
        );
    }

    @Operation(summary = "현금 출금", description = "사용자의 지갑에서 현금을 출금합니다.")
    @PostMapping("/{walletId}/withdrawal")
    public ResponseEntity<ApiResponse<ResWithdrawDto>> withdraw(
            @Parameter(description = "지갑 ID") @PathVariable UUID walletId,
            @Parameter(description = "현금 출금 요청 DTO") @Valid @RequestBody ReqWithdrawDto request,
            @Parameter(hidden = true) @RequestHeader(USERNAME) String userId,
            @Parameter(hidden = true) @RequestHeader(ROLE) String role
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_WITHDRAW_SUCCESS,
                        walletService.withdraw(walletId, request, userId, role)
                )
        );
    }
}
