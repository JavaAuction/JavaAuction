package com.javaauction.auction_service.application.event;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.event.BidAlertEvent;
import com.javaauction.auction_service.domain.event.BidResult;
import com.javaauction.auction_service.infrastructure.client.dto.AlertType;
import com.javaauction.auction_service.infrastructure.client.dto.ReqPostInternalAlertsDtoV1;
import com.javaauction.auction_service.infrastructure.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BidEventHandler {

    private final AuctionKafkaEvent auctionKafkaEvent;
    private final AuctionRepository auctionRepository;

    /**
     * 새 입찰 발생 시 경매 등록자에게 알림 전송
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBidAlert(BidAlertEvent event) {

        BidResult result = event.getResult();
        Auction auction = result.getAuction();

        ReqPostInternalAlertsDtoV1 req = ReqPostInternalAlertsDtoV1.builder()
                .auctionId(auction.getAuctionId())
                .userId(auction.getCreatedBy()) // 경매 등록자에게 알림
                .alertType(AlertType.BID)
                .content(String.format(
                        "%s에 새로운 입찰이 발생했습니다. (입찰가 : %d)",
                        auction.getProductName(),
                        result.getNewBid().getBidPrice()
                ))
                .build();

        log.info("경매 등록자 '{}'에게 입찰 알림 전송", auction.getCreatedBy());

        auctionKafkaEvent.send(req);
    }
}
