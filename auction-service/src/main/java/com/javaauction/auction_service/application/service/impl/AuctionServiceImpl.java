package com.javaauction.auction_service.application.service.impl;

import com.javaauction.auction_service.application.service.AuctionService;
import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.Bid;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import com.javaauction.auction_service.infrastructure.client.AlertFeignClient;
import com.javaauction.auction_service.infrastructure.client.PaymentClient;
import com.javaauction.auction_service.infrastructure.client.ProductFeignClient;
import com.javaauction.auction_service.infrastructure.client.RepProductDto;
import com.javaauction.auction_service.infrastructure.client.dto.AlertType;
import com.javaauction.auction_service.infrastructure.client.dto.DeductType;
import com.javaauction.auction_service.infrastructure.client.dto.ReqCaptureDto;
import com.javaauction.auction_service.infrastructure.client.dto.ReqDeductDto;
import com.javaauction.auction_service.infrastructure.client.dto.ReqPostInternalAlertsDtoV1;
import com.javaauction.auction_service.infrastructure.client.dto.ReqProductStatusUpdateDto;
import com.javaauction.auction_service.infrastructure.client.dto.ReqProductStatusUpdateDto.ProductStatus;
import com.javaauction.auction_service.infrastructure.client.dto.ReqSettleDto;
import com.javaauction.auction_service.infrastructure.client.dto.ReqSettleDto.TransactionType;
import com.javaauction.auction_service.infrastructure.client.dto.ReqValidateDto;
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
import feign.FeignException;
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
    private final AlertFeignClient alertFeignClient;
    private final PaymentClient paymentClient;

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
            .userId(user)
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

        if (auction.getStatus() == AuctionStatus.PENDING) {
            throw new BussinessException(AuctionErrorCode.AUCTION_PENDING);
        }

        if (auction.getStatus() == AuctionStatus.SUCCESSFUL_BID) {
            throw new BussinessException(AuctionErrorCode.AUCTION_SUCCESSFUL_BID);
        }

        if (!auction.getBuyNowEnable()) {
            throw new BussinessException(AuctionErrorCode.AUCTION_BUY_NOW_NOT_AVAILABLE);
        }

        if (user.equals(auction.getUserId())) {
            throw new BussinessException(AuctionErrorCode.AUCTION_BUY_NOW_FORBIDDEN);
        }

        long price = auction.getBuyNowPrice();

        UUID tempBidId = UUID.randomUUID();

        // 자금 precheck
        try {
            paymentClient.validateBalance(new ReqValidateDto(user, price));
        } catch (FeignException e) {
            throw new BussinessException(AuctionErrorCode.AUCTION_INSUFFICIENT_BALANCE);
        }

        // 1) 자금 동결(HOLD)
        ReqDeductDto holdReq = ReqDeductDto.builder()
            .userId(user)
            .transactionType(DeductType.HOLD)
            .deductAmount(price)
            .auctionId(auctionId)
            .bidId(tempBidId)
            .build();

        try {
            paymentClient.deduct(holdReq);
        } catch (FeignException e) {
            throw new BussinessException(AuctionErrorCode.AUCTION_PAYMENT_ERROR);
        }

        // 2) 결제 확정(CAPTURE)

        ReqCaptureDto captureReq = new ReqCaptureDto(auctionId);

        try {
            paymentClient.capture(captureReq);
        } catch (FeignException e) {
            throw new BussinessException(AuctionErrorCode.AUCTION_PAYMENT_ERROR);
        }

        // 3) 경매 상태 변경
        auction.successBid(user, price);

        // 4) 상품 상태 변경
        ReqProductStatusUpdateDto productReq = ReqProductStatusUpdateDto.builder()
            .productStatus(ProductStatus.SOLD)
            .finalPrice(price)
            .build();

        productFeignClient.updateProductStatus(
            auction.getProductId(),
            productReq,
            user
        );

        // 5) 알림 전송(판매자)
        alertFeignClient.createAlert(
            ReqPostInternalAlertsDtoV1.builder()
                .auctionId(auctionId)
                .alertType(AlertType.SUCCESS)
                .userId(auction.getUserId())
                .content(String.format(
                    "%s 상품이 %d원에 즉시 구매되었습니다.",
                    auction.getProductName(), price))
                .build()
        );

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

        if (winningBid == null) {
            auction.failBid();

            ReqPostInternalAlertsDtoV1 req = ReqPostInternalAlertsDtoV1.builder()
                .auctionId(auctionId)
                .alertType(AlertType.FAIL)
                .content(String.format("%s 의 경매가 유찰되었습니다.", auction.getProductName()))
                .userId(auction.getUserId())
                .build();

            alertFeignClient.createAlert(req);

            ReqProductStatusUpdateDto productReq = ReqProductStatusUpdateDto.builder()
                .productStatus(ProductStatus.AUCTION_WAITING)
                .finalPrice(auction.getCurrentPrice())
                .build();

            productFeignClient.updateProductStatus(auction.getProductId(), productReq,
                auction.getSuccessfulBidder());
            return;
        }

        auction.successBid(winningBid.getUserId(), winningBid.getBidPrice());

        ReqProductStatusUpdateDto productReq = ReqProductStatusUpdateDto.builder()
            .productStatus(ProductStatus.SOLD)
            .finalPrice(auction.getCurrentPrice())
            .build();

        productFeignClient.updateProductStatus(auction.getProductId(), productReq,
            auction.getSuccessfulBidder());

        paymentClient.settle(ReqSettleDto.builder()
            .transactionType(TransactionType.HOLD)
            .sellerId(auction.getUserId())
            .buyerId(auction.getSuccessfulBidder())
            .auctionId(auction.getAuctionId())
            .amount(auction.getCurrentPrice())
            .build()
        );

        ReqPostInternalAlertsDtoV1 successReq = ReqPostInternalAlertsDtoV1.builder()
            .auctionId(auctionId)
            .alertType(AlertType.SUCCESS)
            .content(String.format("%s 의 경매가 %s 님에게 %s 원에 낙찰되었습니다.", auction.getProductName(),
                auction.getSuccessfulBidder(),
                auction.getCurrentPrice()))
            .userId(auction.getUserId())
            .build();

        alertFeignClient.createAlert(successReq);

        ReqPostInternalAlertsDtoV1 successBidReq = ReqPostInternalAlertsDtoV1.builder()
            .auctionId(auctionId)
            .alertType(AlertType.SUCCESS)
            .content(String.format("%s 의 경매가 입찰하신 %s 원에 낙찰되었습니다.", auction.getProductName(),
                auction.getCurrentPrice()))
            .userId(auction.getSuccessfulBidder())
            .build();

        alertFeignClient.createAlert(successBidReq);

    }
}
