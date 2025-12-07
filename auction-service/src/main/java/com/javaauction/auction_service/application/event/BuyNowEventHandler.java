package com.javaauction.auction_service.application.event;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.event.BuyNowEvent;
import com.javaauction.auction_service.infrastructure.client.AlertFeignClient;
import com.javaauction.auction_service.infrastructure.client.PaymentClient;
import com.javaauction.auction_service.infrastructure.client.ProductFeignClient;
import com.javaauction.auction_service.infrastructure.client.dto.*;
import com.javaauction.auction_service.infrastructure.client.dto.ReqProductStatusUpdateDto;
import com.javaauction.auction_service.infrastructure.client.dto.ReqProductStatusUpdateDto.ProductStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuyNowEventHandler {

    private final PaymentClient paymentClient;
    private final ProductFeignClient productFeignClient;
    private final AlertFeignClient alertFeignClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BuyNowEvent event) {

        Auction auction = event.getAuction();
        String buyer = event.getBuyerId();
        long price = event.getPrice();
        UUID bidId = event.getTempBidId();

        UUID auctionId = auction.getAuctionId();

        // 1) 자금 동결 (HOLD)
        ReqDeductDto holdReq = ReqDeductDto.builder()
                .userId(buyer)
                .transactionType(DeductType.HOLD)
                .deductAmount(price)
                .auctionId(auctionId)
                .bidId(bidId)
                .build();

        paymentClient.deduct(holdReq);
        log.info("[BuyNow] HOLD success user={}, amount={}", buyer, price);

        // 2) 결제 확정 (CAPTURE)
        ReqCaptureDto captureReq = new ReqCaptureDto(auctionId);
        paymentClient.capture(captureReq);
        log.info("[BuyNow] CAPTURE success auctionId={}", auctionId);

        // 3) 상품 상태 업데이트
        ReqProductStatusUpdateDto productReq = ReqProductStatusUpdateDto.builder()
                .productStatus(ProductStatus.SOLD)
                .finalPrice(price)
                .build();

        productFeignClient.updateProductStatus(
                auction.getProductId(),
                productReq,
                buyer
        );

        // 4) 알림 발송 (판매자)
        String sellerContent = String.format(
                "%s 상품이 %d원에 즉시 구매되었습니다.",
                auction.getProductName(), price
        );

        alertFeignClient.createAlert(
                ReqPostInternalAlertsDtoV1.builder()
                        .auctionId(auctionId)
                        .alertType(AlertType.SUCCESS)
                        .userId(auction.getUserId())
                        .content(sellerContent)
                        .build()
        );
    }
}

