package com.javaauction.auction_service.presentation.controller;

import com.javaauction.auction_service.application.service.AuctionService;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import com.javaauction.auction_service.presentation.advice.AuctionSuccessCode;
import com.javaauction.auction_service.presentation.dto.request.ReqCreateAuctionDto;
import com.javaauction.auction_service.presentation.dto.response.ResCreatedAuctionDto;
import com.javaauction.auction_service.presentation.dto.response.ResGetAuctionDto;
import com.javaauction.auction_service.presentation.dto.response.ResGetAuctionsDto;
import com.javaauction.global.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<ApiResponse<ResCreatedAuctionDto>> createAuction(
        @Valid @RequestBody ReqCreateAuctionDto req
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                AuctionSuccessCode.AUCTION_CREATE_SUCCESS,
                auctionService.createAuction(req)
            ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ResGetAuctionsDto>> getAuctions(
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable,
        @RequestParam(required = false) AuctionStatus status,
        @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(ApiResponse.success(AuctionSuccessCode.AUCTION_FIND_SUCCESS,
            auctionService.getAuctions(pageable, status, keyword)));
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<ApiResponse<ResGetAuctionDto>> getAuction(
        @PathVariable UUID auctionId
    ) {
        return ResponseEntity.ok(ApiResponse.success(AuctionSuccessCode.AUCTION_FIND_SUCCESS,
            auctionService.getAuction(auctionId)));
    }

    @PostMapping("/{auctionId}/re-register")
    public ResponseEntity<ApiResponse<Void>> reRegisterAuction(
        @PathVariable("auctionId") UUID id
    ) {
        auctionService.reRegisterAuction(id);
        return ResponseEntity.ok(
            ApiResponse.success(AuctionSuccessCode.AUCTION_RE_REGISTER_SUCCESS));
    }


    @DeleteMapping("/{auctionId}")
    public ResponseEntity<ApiResponse<Void>> deleteAuction(
        @PathVariable("auctionId") UUID id
    ) {
        auctionService.deleteAuction(id, "temp");
        return ResponseEntity.ok(
            ApiResponse.success(AuctionSuccessCode.AUCTION_DELETED));
    }


    @PutMapping("/{auctionId}")
    public ResponseEntity<ApiResponse<Void>> updateAuction(
        @PathVariable("auctionId") UUID id
    ) {
        auctionService.updateAuction(id, "temp");
        return ResponseEntity.ok(
            ApiResponse.success(AuctionSuccessCode.AUCTION_UPDATED));
    }

}
