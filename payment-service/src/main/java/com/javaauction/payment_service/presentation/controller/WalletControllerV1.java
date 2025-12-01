package com.javaauction.payment_service.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.payment_service.application.service.WalletServiceV1;
import com.javaauction.payment_service.presentation.advice.PaymentSuccessCode;
import com.javaauction.payment_service.presentation.dto.request.ReqChargeDto;
import com.javaauction.payment_service.presentation.dto.response.ResChargeDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/wallets/{walletId}")
public class WalletControllerV1 {

    private final WalletServiceV1 walletService;

    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<ResChargeDto>> charge(
            @PathVariable UUID walletId, @Valid @RequestBody ReqChargeDto request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        PaymentSuccessCode.PAYMENT_CHARGE_SUCCESS,
                        walletService.charge(walletId, request)
                )
        );
    }
}
