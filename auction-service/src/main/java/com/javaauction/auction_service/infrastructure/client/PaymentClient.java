package com.javaauction.auction_service.infrastructure.client;

import com.javaauction.auction_service.infrastructure.client.dto.ReqCaptureDto;
import com.javaauction.auction_service.infrastructure.client.dto.ReqDeductDto;
import com.javaauction.auction_service.infrastructure.client.dto.ReqSettleDto;
import com.javaauction.auction_service.infrastructure.client.dto.ReqValidateDto;
import com.javaauction.global.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/internal/wallets/validations")
    ApiResponse<?> validateBalance(@RequestBody ReqValidateDto request);

    @PostMapping("/internal/wallets/deductions")
    ApiResponse<?> deduct(@RequestBody ReqDeductDto request);

    @PostMapping("/internal/wallets/transactions/capture")
    ApiResponse<?> capture(@RequestBody ReqCaptureDto request);

    @PostMapping("/internal/wallets/transactions/settlement")
    void settle(@Valid @RequestBody ReqSettleDto request);
}

