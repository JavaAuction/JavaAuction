package com.javaauction.auction_service.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/v1/auction")
public class AuctionAdminController {

    @PatchMapping("/{auctionId}/status")
    public ResponseEntity<ApiResponse<Void>> UpdateAuctionStatus(
        @PathVariable("auctionId") UUID id
    ) {
        return null;
    }

}
