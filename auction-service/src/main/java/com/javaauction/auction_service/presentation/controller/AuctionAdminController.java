package com.javaauction.auction_service.presentation.controller;

import com.javaauction.auction_service.application.service.AuctionService;
import com.javaauction.auction_service.infrastructure.config.check.IsAdmin;
import com.javaauction.auction_service.presentation.advice.AuctionSuccessCode;
import com.javaauction.auction_service.presentation.dto.request.ReqUpdateStatusAuctionDto;
import com.javaauction.global.presentation.response.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auctions")
public class AuctionAdminController {

    private final AuctionService auctionService;

    @IsAdmin
    @PatchMapping("/{auctionId}/status")
    public ResponseEntity<ApiResponse<Void>> UpdateAuctionStatus(
        @PathVariable("auctionId") UUID id,
        @RequestBody ReqUpdateStatusAuctionDto req,
        @RequestHeader("X-User-Username") String username,
        @RequestHeader("X-User-Role") String role
    ) {
        auctionService.UpdateAuctionStatus(id, req, username);

        return ResponseEntity.ok(
            ApiResponse.success(AuctionSuccessCode.AUCTION_STATUS_UPDATED));
    }

    @IsAdmin
    @DeleteMapping("/{auctionId}")
    public ResponseEntity<ApiResponse<Void>> deleteAuction(
        @PathVariable("auctionId") UUID id,
        @RequestHeader("X-User-Username") String username
    ) {
        auctionService.deleteAuction(id, username);
        return ResponseEntity.ok(
            ApiResponse.success(AuctionSuccessCode.AUCTION_DELETED));
    }

}
