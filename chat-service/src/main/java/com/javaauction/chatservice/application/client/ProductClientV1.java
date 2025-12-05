package com.javaauction.chatservice.application.client;

import com.javaauction.chatservice.presentation.dto.response.RepGetProductsDtoV1;
import com.javaauction.global.presentation.response.ApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClientV1 {
    @GetMapping("/v1/products/{productId}")
    ResponseEntity<ApiResponse<RepGetProductsDtoV1>> getProduct(@PathVariable UUID productId);
}
