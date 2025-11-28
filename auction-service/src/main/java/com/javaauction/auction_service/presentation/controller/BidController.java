package com.javaauction.auction_service.presentation.controller;

import com.javaauction.auction_service.application.service.BidService;
import com.javaauction.auction_service.domain.entity.Bid;
import com.javaauction.auction_service.infrastructure.repository.BidRepository;
import com.javaauction.auction_service.presentation.advice.BidSuccessCode;
import com.javaauction.auction_service.presentation.dto.request.ReqPostBidDto;
import com.javaauction.auction_service.presentation.dto.response.ResPostBidDto;
import com.javaauction.global.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auctions")
public class BidController {

    private final BidService bidService;
    private final BidRepository bidRepository;

    /**
     * 입찰 생성
     * POST /v1/auctions/{auctionId}/bid
     */
    @PostMapping("/{auctionId}/bid")
    public ResponseEntity<ApiResponse<ResPostBidDto>> createBid(
            @PathVariable("auctionId") UUID auctionId,
            @RequestHeader("X-User-Username") String userId,
            @RequestBody ReqPostBidDto req
    ) {
        var result = bidService.placeBid(
                auctionId,
                userId,
                req.getBidPrice()
        );

        var newBid = result.getNewBid();
        var auction = result.getAuction();

        ResPostBidDto response = ResPostBidDto.builder()
                .bidId(newBid.getBidId())
                .productId(auction.getProductId())
                .bidPrice(newBid.getBidPrice())
                .bidStatus(newBid.getStatus())
                .createdAt(newBid.getCreatedAt())
                .build();

        return ResponseEntity
                .status(BidSuccessCode.BID_CREATE_SUCCESS.getStatus())
                .body(ApiResponse.success(BidSuccessCode.BID_CREATE_SUCCESS, response));
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

    // 상태 테스트용 조회 api - 추후 삭제 예정
    @GetMapping("/debug/bids")
    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

}

