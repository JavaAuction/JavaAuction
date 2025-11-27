package com.javaauction.auction_service.presentation.controller;

import com.javaauction.auction_service.presentation.dto.request.ReqPostBidDto;
import com.javaauction.auction_service.presentation.dto.response.ResPostBidDto;
import com.javaauction.global.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auctions")
public class BidController {

    /**
     * 입찰 생성
     * POST /v1/auctions/{auctionId}/bid
     */
    @PostMapping("/{auctionId}/bid")
    public ResponseEntity<ApiResponse<ResPostBidDto>> createBid(
            @PathVariable("auctionId") UUID auctionId,
            @RequestBody ReqPostBidDto req
    ) {
        return null;
    }

    /**
     * 특정 경매의 입찰 목록 조회
     * GET /v1/auctions/{auctionId}/bid
     */
    @GetMapping("/{auctionId}/bid")
    public ResponseEntity<ApiResponse<Object>> getBids(
            @PathVariable("auctionId") UUID auctionId
    ) {
        return null;
    }
}

