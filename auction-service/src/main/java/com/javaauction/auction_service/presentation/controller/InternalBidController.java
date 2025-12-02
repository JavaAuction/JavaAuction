package com.javaauction.auction_service.presentation.controller;
import com.javaauction.auction_service.application.service.BidService;
import com.javaauction.auction_service.presentation.advice.BidSuccessCode;
import com.javaauction.auction_service.presentation.dto.response.internal.ResInternalBidsDto;
import com.javaauction.global.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/bids")
public class InternalBidController {

    private final BidService bidService;

    /**
     * 내부 입찰 목록 조회
     * GET /internal/bids?userId=...
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ResInternalBidsDto>> getBidsInternal(
            @RequestParam(value = "userId", required = false) String userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(BidSuccessCode.BID_FIND_SUCCESS,
                bidService.internalGetBids(userId)));
    }
}
