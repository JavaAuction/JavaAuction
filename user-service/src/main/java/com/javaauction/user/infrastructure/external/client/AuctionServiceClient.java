package com.javaauction.user.infrastructure.external.client;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.user.infrastructure.external.dto.ResInternalBidsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("auction-service")
public interface AuctionServiceClient {
    @GetMapping("/internal/bids")
    ResponseEntity<ApiResponse<ResInternalBidsDto>> getBidsInternal(
            @RequestParam(value = "userId", required = false) String userId);
}
