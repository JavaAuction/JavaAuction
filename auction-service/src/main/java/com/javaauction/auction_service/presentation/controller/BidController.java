package com.javaauction.auction_service.presentation.controller;

import com.javaauction.auction_service.application.service.BidService;
import com.javaauction.auction_service.domain.entity.Bid;
import com.javaauction.auction_service.infrastructure.repository.BidRepository;
import com.javaauction.auction_service.presentation.advice.BidSuccessCode;
import com.javaauction.auction_service.presentation.dto.request.ReqPostBidDto;
import com.javaauction.auction_service.presentation.dto.response.ResGetBidsDto;
import com.javaauction.auction_service.presentation.dto.response.ResPostBidDto;
import com.javaauction.global.presentation.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auctions")
@Tag(name = "입찰 컨트롤러", description = "경매에 참여하는 사용자가 이용")
public class BidController {

    private final BidService bidService;
    private final BidRepository bidRepository;

    /**
     * 입찰 생성 POST /v1/auctions/{auctionId}/bid
     */
    @PostMapping("/{auctionId}/bid")
    @Operation(summary = "입찰 생성", description = "특정 경매에 대해 새로운 입찰을 생성합니다.")
    public ResponseEntity<ApiResponse<ResPostBidDto>> createBid(
        @PathVariable("auctionId") UUID auctionId,
        @RequestHeader("X-User-Username") String userId,
        @RequestHeader("X-User-Role") String role,
        @RequestBody ReqPostBidDto req
    ) {
        var result = bidService.placeBid(
            auctionId,
            userId,
            role,
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
     * 특정 경매의 입찰 목록 조회 GET /v1/auctions/{auctionId}/bid
     */
    @GetMapping("/{auctionId}/bids")
    @Operation(summary = "경매 입찰 목록 조회", description = "특정 경매에 대한 유효한 입찰 목록을 상위 5개까지 조회합니다.")
    public ResponseEntity<ApiResponse<ResGetBidsDto>> getBids(
        @PathVariable("auctionId") UUID auctionId
    ) {
        return ResponseEntity.ok(ApiResponse.success(BidSuccessCode.BID_FIND_SUCCESS,
            bidService.getBids(auctionId)));
    }

    // 상태 테스트용 조회 api - 추후 삭제 예정
    @GetMapping("/debug/bids")
    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

}

