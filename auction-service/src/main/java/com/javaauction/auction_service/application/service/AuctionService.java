package com.javaauction.auction_service.application.service;

import com.javaauction.auction_service.presentation.dto.request.ReqCreateAuctionDto;
import com.javaauction.auction_service.presentation.dto.response.ResCreatedAuctionDto;
import com.javaauction.auction_service.presentation.dto.response.ResGetAuctionDto;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public interface AuctionService {

    ResCreatedAuctionDto createAuction(ReqCreateAuctionDto req);

    @Transactional(readOnly = true)
    ResGetAuctionDto getAuction(UUID auctionId);
}
