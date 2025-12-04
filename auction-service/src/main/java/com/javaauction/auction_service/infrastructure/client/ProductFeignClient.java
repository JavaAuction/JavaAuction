package com.javaauction.auction_service.infrastructure.client;

import com.javaauction.auction_service.infrastructure.client.dto.ReqProductStatusUpdateDto;
import com.javaauction.global.presentation.response.ApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "product-service")
public interface ProductFeignClient {

    @GetMapping("/v1/products/{productId}")
    ResponseEntity<ApiResponse<RepProductDto>> getProduct(@PathVariable UUID productId);

    @PutMapping("/v1/products/{productId}/status")
    void updateProductStatus(
        @PathVariable UUID productId,
        @RequestBody ReqProductStatusUpdateDto request,
        @RequestHeader("X-User-Username") String username
    );
}
