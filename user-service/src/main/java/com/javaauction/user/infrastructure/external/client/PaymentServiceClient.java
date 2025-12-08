package com.javaauction.user.infrastructure.external.client;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.user.infrastructure.external.dto.ReqCreateWalletDto;
import com.javaauction.user.infrastructure.external.dto.ResCreateWalletDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
    @PostMapping("/internal/wallets")
    ResponseEntity<ApiResponse<ResCreateWalletDto>> create(@Valid @RequestBody ReqCreateWalletDto request);
}
