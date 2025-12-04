package com.javaauction.auction_service.infrastructure.client;


import com.javaauction.auction_service.infrastructure.client.dto.ReqDeductDto;
import com.javaauction.auction_service.infrastructure.client.dto.ReqValidateDto;
import com.javaauction.auction_service.infrastructure.client.dto.ResDeductDto;
import com.javaauction.global.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentFeignClient {

    @PostMapping("/internal/v1/wallets/deductions")
    ResponseEntity<ApiResponse<ResDeductDto>> deduct(
        @Valid @RequestBody ReqDeductDto request);

    @PostMapping("/internal/v1/wallets/validations")
    void validate(@Valid @RequestBody ReqValidateDto request);
}
