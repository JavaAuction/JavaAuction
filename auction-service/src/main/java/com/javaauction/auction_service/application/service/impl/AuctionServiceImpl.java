package com.javaauction.auction_service.application.service.impl;

import com.javaauction.auction_service.application.service.AuctionService;
import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import com.javaauction.auction_service.infrastructure.repository.AuctionRepository;
import com.javaauction.auction_service.presentation.advice.AuctionErrorCode;
import com.javaauction.auction_service.presentation.dto.request.ReqCreateAuctionDto;
import com.javaauction.auction_service.presentation.dto.response.ResCreatedAuctionDto;
import com.javaauction.global.presentation.exception.BussinessException;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;

    @Override
    @Transactional
    public ResCreatedAuctionDto createAuction(ReqCreateAuctionDto req) {
        // 통신 후에 상품 있는지 + 상품만든사람이랑 같은 사람인지 확인하기

        if (auctionRepository.existsByProductIdAndDeletedAtIsNull(req.productId())) {
            throw new BussinessException(AuctionErrorCode.AUCTION_ALREADY_EXIST);
        }
        if (req.endAt().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BussinessException(AuctionErrorCode.AUCTION_END_LATER);
        }

        Auction auction = Auction.builder()
            .productId(req.productId())
            .startPrice(req.startPrice())
            .unit(req.unit())
            .buyNowEnable(req.buyNowEnable())
            .buyNowPrice(req.buyNowPrice())
            .endedAt(req.endAt())
            .status(AuctionStatus.IN_PROGRESS)
            .build();

        // 나중에 지울 예정
        auction.setCreate(Instant.now(), "system");
        Auction saveAuction = auctionRepository.save(auction);

        return ResCreatedAuctionDto.from(saveAuction);

    }


}
