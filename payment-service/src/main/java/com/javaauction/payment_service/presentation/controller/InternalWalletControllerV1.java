package com.javaauction.payment_service.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.payment_service.application.service.WalletServiceV1;
import com.javaauction.payment_service.presentation.dto.request.ReqCreateDto;
import com.javaauction.payment_service.presentation.dto.request.ReqDeductDto;
import com.javaauction.payment_service.presentation.dto.request.ReqDeleteDto;
import com.javaauction.payment_service.presentation.dto.request.ReqValidateDto;
import com.javaauction.payment_service.presentation.dto.response.ResCreateDto;
import com.javaauction.payment_service.presentation.dto.response.ResDeductDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.javaauction.payment_service.presentation.advice.PaymentErrorCode.WALLET_INSUFFICIENT_BALANCE;
import static com.javaauction.payment_service.presentation.advice.PaymentSuccessCode.*;

@Tag(name = "Internal Wallet", description = "지갑 내부 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/wallets")
public class InternalWalletControllerV1 {

    private final WalletServiceV1 walletService;

    @Operation(summary = "지갑 생성", description = "회원 가입 시 자동으로 지갑 생성을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ResCreateDto>> create(
            @Parameter(description = "지갑 생성 요청 DTO") @Valid @RequestBody ReqCreateDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        WALLET_CREATE_SUCCESS,
                        walletService.create(request)
                )
        );
    }

    @Operation(summary = "잔액 차감", description = "입찰 또는 즉시 구매 시 지갑에서 잔액이 차감됩니다.")
    @PostMapping("/deductions")
    public ResponseEntity<ApiResponse<ResDeductDto>> deduct(
            @Parameter(description = "잔액 차감 요청 DTO") @Valid @RequestBody ReqDeductDto request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        WALLET_DEDUCT_SUCCESS,
                        walletService.deduct(request)
                )
        );
    }

    @Operation(summary = "잔액 검증", description = "입찰 전 지갑에 충분한 잔액이 있는지 검증합니다.")
    @PostMapping("/validations")
    public ResponseEntity<ApiResponse<?>> validate(
            @Parameter(description = "잔액 검증 요청 DTO") @Valid @RequestBody ReqValidateDto request
    ) {

        return walletService.validate(request)
                ? ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(WALLET_VALIDATE_SUCCESS))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(WALLET_INSUFFICIENT_BALANCE));
    }

    @Operation(summary = "지갑 삭제", description = "회원 탈퇴 시 자동으로 지갑이 해지됩니다.")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> delete(@Valid @RequestBody ReqDeleteDto request) {
        walletService.delete(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(WALLET_DELETE_SUCCESS)
        );
    }
}
