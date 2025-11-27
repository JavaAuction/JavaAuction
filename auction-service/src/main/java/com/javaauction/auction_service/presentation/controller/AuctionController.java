package com.javaauction.auction_service.presentation.controller;

import com.javaauction.auction_service.presentation.controller.dto.ReqCreateAuctionDto;
import com.javaauction.auction_service.presentation.controller.dto.ResCreatedAuctionDto;
import com.javaauction.auction_service.presentation.controller.dto.ResGetAuctionDto;
import com.javaauction.auction_service.presentation.controller.dto.ResGetAuctionsDto;
import com.javaauction.global.presentation.response.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auction")
public class AuctionController {

    @PostMapping
    public ResponseEntity<ApiResponse<ResCreatedAuctionDto>> createAuction(
        @RequestBody ReqCreateAuctionDto req
    ) {
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ResGetAuctionsDto>> getAuctions(
    ) {
        return null;
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<ApiResponse<ResGetAuctionDto>> getAuction(
    ) {
        return null;
    }

    @PostMapping("/{auctionId}/re-register")
    public ResponseEntity<ApiResponse<Void>> reRegisterAuction(
        @PathVariable("auctionId") UUID id
    ) {
        return null;
    }


    @DeleteMapping("/{auctionId}")
    public ResponseEntity<ApiResponse<Void>> deleteAuction(
        @PathVariable("auctionId") UUID id
    ) {
        return null;
    }


    @PutMapping("/{auctionId}")
    public ResponseEntity<ApiResponse<Void>> updateAuction(
        @PathVariable("auctionId") UUID id
    ) {
        return null;
    }


    @PatchMapping("/{auctionId}")
    public ResponseEntity<ApiResponse<Void>> UpdateAuctionStatus(
        @PathVariable("auctionId") UUID id
    ) {
        return null;
    }

}
