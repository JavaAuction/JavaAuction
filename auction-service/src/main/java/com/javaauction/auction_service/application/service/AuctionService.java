package com.javaauction.auction_service.application.service;

import com.javaauction.auction_service.presentation.dto.request.ReqCreateAuctionDto;
import com.javaauction.auction_service.presentation.dto.response.ResCreatedAuctionDto;

public interface AuctionService {

    ResCreatedAuctionDto createAuction(ReqCreateAuctionDto req);
}
