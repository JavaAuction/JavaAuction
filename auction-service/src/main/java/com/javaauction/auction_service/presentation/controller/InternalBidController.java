package com.javaauction.auction_service.presentation.controller;

import com.javaauction.auction_service.application.service.BidService;
import com.javaauction.auction_service.presentation.advice.BidSuccessCode;
import com.javaauction.auction_service.presentation.dto.response.internal.ResInternalBidsDto;
import com.javaauction.global.presentation.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/bids")
@Tag(name = "내부 통신 입찰 컨트롤러", description = "타 서비스에서 이용")
public class InternalBidController {

    private final BidService bidService;

    /**
     * 내부 입찰 목록 조회 GET /internal/bids?userId=...
     */
    @GetMapping
    @Operation(summary = "입찰 목록 조회", description = "특정 유저에 대한 입찰 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<ResInternalBidsDto>> getBidsInternal(
        @RequestParam(value = "userId", required = false) String userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(BidSuccessCode.BID_FIND_SUCCESS,
            bidService.internalGetBids(userId)));
    }
}
