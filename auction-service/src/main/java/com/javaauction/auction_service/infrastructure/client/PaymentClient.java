package com.javaauction.auction_service.infrastructure.client;

import com.javaauction.auction_service.infrastructure.client.dto.ReqValidateDto;
import com.javaauction.global.presentation.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/internal/wallets/validations")
    ApiResponse<?> validateBalance(@RequestBody ReqValidateDto request);
}

