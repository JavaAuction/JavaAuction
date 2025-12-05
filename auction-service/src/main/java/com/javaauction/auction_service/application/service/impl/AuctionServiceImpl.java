package com.javaauction.auction_service.application.service.impl;

import com.javaauction.auction_service.application.service.AuctionService;
import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.Bid;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import com.javaauction.auction_service.infrastructure.client.ProductFeignClient;
import com.javaauction.auction_service.infrastructure.client.RepProductDto;
import com.javaauction.auction_service.infrastructure.client.dto.ReqProductStatusUpdateDto;
import com.javaauction.auction_service.infrastructure.client.dto.ReqProductStatusUpdateDto.ProductStatus;
import com.javaauction.auction_service.infrastructure.repository.AuctionRepository;
import com.javaauction.auction_service.infrastructure.repository.BidRepository;
import com.javaauction.auction_service.presentation.advice.AuctionErrorCode;
import com.javaauction.auction_service.presentation.dto.request.ReqCreateAuctionDto;
import com.javaauction.auction_service.presentation.dto.request.ReqUpdateAuctionDto;
import com.javaauction.auction_service.presentation.dto.request.ReqUpdateStatusAuctionDto;
import com.javaauction.auction_service.presentation.dto.response.ResBuyNowDto;
import com.javaauction.auction_service.presentation.dto.response.ResCreatedAuctionDto;
import com.javaauction.auction_service.presentation.dto.response.ResGetAuctionDto;
import com.javaauction.auction_service.presentation.dto.response.ResGetAuctionsDto;
import com.javaauction.global.presentation.exception.BussinessException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final ProductFeignClient productFeignClient;

    @Override
    @Transactional
    public ResCreatedAuctionDto createAuction(ReqCreateAuctionDto req, String user) {

        RepProductDto product = productFeignClient.getProduct(req.productId()).getBody().getData();

        if (!product.userId().equals(user)) {
            throw new BussinessException(AuctionErrorCode.AUCTION_PRODUCT_FORBIDDEN);
        }

        if (auctionRepository.existsByProductIdAndDeletedAtIsNull(req.productId())) {
            throw new BussinessException(AuctionErrorCode.AUCTION_ALREADY_EXIST);
        }
        if (req.endAt().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BussinessException(AuctionErrorCode.AUCTION_END_LATER);
        }

        Auction auction = Auction.builder()
            .productId(req.productId())
            .productName(product.name())
            .startPrice(req.startPrice())
            .unit(req.unit())
            .buyNowEnable(req.buyNowEnable())
            .buyNowPrice(req.buyNowPrice())
            .endedAt(req.endAt())
            .status(AuctionStatus.IN_PROGRESS)
            .build();

        ReqProductStatusUpdateDto productReq = ReqProductStatusUpdateDto.builder()
            .productStatus(ProductStatus.AUCTION_RUNNING)
            .finalPrice(0L)
            .build();

        auction.setCreate(Instant.now(), user);
        Auction saveAuction = auctionRepository.save(auction);

        productFeignClient.updateProductStatus(product.productId(), productReq, user);

        return ResCreatedAuctionDto.from(saveAuction);

    }

    @Transactional(readOnly = true)
    @Override
    public ResGetAuctionDto getAuction(UUID auctionId) {

        Auction auction = auctionRepository.findByAuctionIdAndDeletedAtIsNull(auctionId)
            .orElseThrow(() -> new BussinessException(AuctionErrorCode.AUCTION_NOT_FOUND)
            );

        return ResGetAuctionDto.from(auction);
    }

    @Transactional(readOnly = true)
    @Override
    public ResGetAuctionsDto getAuctions(Pageable pageable, AuctionStatus status, String keyword) {

        Page<Auction> auctions = auctionRepository.auctions(pageable, status, keyword);

        return ResGetAuctionsDto.from(auctions);
    }

    @Transactional
    @Override
    public void reRegisterAuction(UUID auctionId, String user) {

        Auction auction = auctionRepository.findByAuctionIdAndDeletedAtIsNull(auctionId)
            .orElseThrow(() -> new BussinessException(AuctionErrorCode.AUCTION_NOT_FOUND));

        switch (auction.getStatus()) {
            case IN_PROGRESS -> throw new BussinessException(AuctionErrorCode.AUCTION_IN_PROGRESS);
            case SUCCESSFUL_BID ->
                throw new BussinessException(AuctionErrorCode.AUCTION_SUCCESSFUL_BID);
//            case PENDING -> throw new BussinessException(AuctionErrorCode.AUCTION_PENDING);
            default -> {
                auction.reRegister();
            }
        }

        ReqProductStatusUpdateDto productReq = ReqProductStatusUpdateDto.builder()
            .productStatus(ProductStatus.AUCTION_RUNNING)
            .finalPrice(auction.getCurrentPrice())
            .build();

        productFeignClient.updateProductStatus(auction.getProductId(), productReq, user);
    }

    @Transactional
    @Override
    public void deleteAuction(UUID auctionId, String user) {
        Auction auction = auctionRepository.findByAuctionIdAndDeletedAtIsNull(auctionId)
            .orElseThrow(() -> new BussinessException(AuctionErrorCode.AUCTION_NOT_FOUND));

        if (auction.getStatus() == AuctionStatus.IN_PROGRESS) {
            throw new BussinessException(AuctionErrorCode.AUCTION_IN_PROGRESS);
        }

        auction.softDelete(Instant.now(), user);
    }


    @Transactional
    @Override
    public void updateAuction(UUID auctionId, String user, ReqUpdateAuctionDto req) {
        Auction auction = auctionRepository.findByAuctionIdAndDeletedAtIsNull(auctionId)
            .orElseThrow(() -> new BussinessException(AuctionErrorCode.AUCTION_NOT_FOUND));

        if (!(auction.getStatus() == AuctionStatus.PENDING)) {
            throw new BussinessException(AuctionErrorCode.AUCTION_NOT_PENDING);
        }

        auction.update(
            req.successfulBidder() != null ? req.successfulBidder() : auction.getSuccessfulBidder(),
            req.startPrice() != null ? req.startPrice() : auction.getStartPrice(),
            req.unit() != null ? req.unit() : auction.getUnit(),
            req.buyNowEnable() != null ? req.buyNowEnable() : auction.getBuyNowEnable(),
            req.buyNowPrice() != null ? req.buyNowPrice() : auction.getBuyNowPrice(),
            req.endedAt() != null ? req.endedAt() : auction.getEndedAt()
        );

        ProductStatus status = null;

        switch (auction.getStatus()) {
            case IN_PROGRESS -> status = ProductStatus.AUCTION_RUNNING;
            case SUCCESSFUL_BID -> status = ProductStatus.SOLD;
            default -> {
                status = ProductStatus.AUCTION_WAITING;
            }
        }

        ReqProductStatusUpdateDto productReq = ReqProductStatusUpdateDto.builder()
            .productStatus(status)
            .finalPrice(auction.getCurrentPrice())
            .build();

        productFeignClient.updateProductStatus(auction.getProductId(), productReq, user);
    }

    @Transactional
    @Override
    public void UpdateAuctionStatus(UUID auctionId, ReqUpdateStatusAuctionDto req, String user) {
        Auction auction = auctionRepository.findByAuctionIdAndDeletedAtIsNull(auctionId)
            .orElseThrow(() -> new BussinessException(AuctionErrorCode.AUCTION_NOT_FOUND));
        ProductStatus status = null;

        switch (auction.getStatus()) {
            case IN_PROGRESS -> status = ProductStatus.AUCTION_RUNNING;
            case SUCCESSFUL_BID -> status = ProductStatus.SOLD;
            default -> {
                status = ProductStatus.AUCTION_WAITING;
            }
        }

        ReqProductStatusUpdateDto productReq = ReqProductStatusUpdateDto.builder()
            .productStatus(status)
            .finalPrice(0L)
            .build();

        productFeignClient.updateProductStatus(auction.getProductId(), productReq, user);

        auction.updateStatus(req.status());
    }

    @Transactional
    @Override
    public ResBuyNowDto buyNow(UUID auctionId, String user) {
        Auction auction = auctionRepository.findByAuctionIdAndDeletedAtIsNull(auctionId)
            .orElseThrow(() -> new BussinessException(AuctionErrorCode.AUCTION_NOT_FOUND));

        if ((auction.getStatus() == AuctionStatus.SUCCESSFUL_BID)) {
            throw new BussinessException(AuctionErrorCode.AUCTION_SUCCESSFUL_BID);
        }

        if (!auction.getBuyNowEnable()) {
            throw new BussinessException(AuctionErrorCode.AUCTION_BUY_NOW_NOT_AVAILABLE);
        }

        long price = auction.getBuyNowPrice();

        // TODO: 결제 기능 추후 연결
        // 로그 - 추후 삭제 예정
        log.info("[BuyNow] precheck(userId={}, auctionId={}, price={})", user, auctionId, price);
        log.info("[BuyNow] hold(userId={}, auctionId={}, price={})", user, auctionId, price);

        // TODO: 경매 종료, 낙찰 기능 추후 연결
        // 로그 - 추후 삭제 예정
        log.info("[BuyNow] closeAuction(auctionId={}, winnerId={}, finalPrice={})",
            auctionId, user, price);

        return ResBuyNowDto.builder()
            .auctionId(auctionId)
            .productId(auction.getProductId())
            .buyerId(user)
            .finalPrice(price)
            .purchasedAt(Instant.now())
            .build();
    }

    @Transactional
    @Override
    public void auctionEnds(UUID auctionId) {

        Auction auction = auctionRepository.findByAuctionIdAndDeletedAtIsNull(auctionId)
            .orElseThrow(() -> new BussinessException(AuctionErrorCode.AUCTION_NOT_FOUND));

        Bid winningBid = bidRepository.findTopByAuctionIdOrderByBidPriceDesc(auctionId)
            .orElse(null);

        ProductStatus status = null;

        switch (auction.getStatus()) {
            case IN_PROGRESS -> status = ProductStatus.AUCTION_RUNNING;
            case SUCCESSFUL_BID -> status = ProductStatus.SOLD;
            default -> status = ProductStatus.AUCTION_WAITING;
        }

        ReqProductStatusUpdateDto productReq = ReqProductStatusUpdateDto.builder()
            .productStatus(status)
            .finalPrice(auction.getCurrentPrice())
            .build();

        productFeignClient.updateProductStatus(auction.getProductId(), productReq,
            auction.getSuccessfulBidder());
        if (winningBid == null) {
            auction.faileBid();
            return;
        }

        auction.successBid(winningBid.getUserId(), winningBid.getBidPrice());
    }
}
