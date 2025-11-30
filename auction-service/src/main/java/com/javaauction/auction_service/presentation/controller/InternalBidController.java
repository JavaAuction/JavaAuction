package com.javaauction.auction_service.presentation.controller;
import com.javaauction.global.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/bids")
public class InternalBidController {

    /**
     * 내부 입찰 목록 조회
     * GET /internal/bids?userId=...
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getBidsInternal(
            @RequestParam(value = "userId", required = false) String userId
    ) {
        return null;
    }
}
