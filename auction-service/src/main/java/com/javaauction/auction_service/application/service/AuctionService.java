package com.javaauction.auction_service.application.service;

import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import com.javaauction.auction_service.presentation.dto.request.ReqCreateAuctionDto;
import com.javaauction.auction_service.presentation.dto.request.ReqUpdateAuctionDto;
import com.javaauction.auction_service.presentation.dto.request.ReqUpdateStatusAuctionDto;
import com.javaauction.auction_service.presentation.dto.response.ResCreatedAuctionDto;
import com.javaauction.auction_service.presentation.dto.response.ResGetAuctionDto;
import com.javaauction.auction_service.presentation.dto.response.ResGetAuctionsDto;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface AuctionService {

    ResCreatedAuctionDto createAuction(ReqCreateAuctionDto req);

    @Transactional(readOnly = true)
    ResGetAuctionDto getAuction(UUID auctionId);

    @Transactional(readOnly = true)
    ResGetAuctionsDto getAuctions(Pageable pageable, AuctionStatus status, String keyword);

    @Transactional
    void reRegisterAuction(UUID auctionId);

    @Transactional
    void deleteAuction(UUID auctionId, String user);

    @Transactional
    void updateAuction(UUID auctionId, String user, ReqUpdateAuctionDto req);

    @Transactional
    void UpdateAuctionStatus(UUID auctionId, ReqUpdateStatusAuctionDto req);
}
