package com.javaauction.auction_service.application.event;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.Bid;
import com.javaauction.auction_service.domain.event.BidAlertEvent;
import com.javaauction.auction_service.domain.event.BidResult;
import com.javaauction.auction_service.domain.event.OldBidReleaseEvent;
import com.javaauction.auction_service.infrastructure.client.AlertClient;
import com.javaauction.auction_service.infrastructure.client.dto.AlertType;
import com.javaauction.auction_service.infrastructure.client.dto.ReqAlertDto;
import com.javaauction.auction_service.infrastructure.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BidEventHandler {

    private final BidRepository bidRepository;
    private final AlertClient alertClient;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOldBidRelease(OldBidReleaseEvent event) {

        var result = event.getBidResult();

        String oldUserId = result.getOldUserId();
        Long oldPrice    = result.getOldPrice();
        UUID auctionId   = result.getAuctionId();

        if (oldUserId == null || oldPrice == null) return;

        Bid oldBid = bidRepository.findHeldBidExact(auctionId, oldUserId, oldPrice)
                .orElse(null);

        log.info("release event => oldUserId={}, newUserId={}",
                result.getOldUserId(), result.getNewBid().getUserId());

        if (oldBid == null) return;

        oldBid.release();
        bidRepository.save(oldBid);
    }

    /**
     * 새 입찰 발생 시 경매 등록자에게 알림 전송
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBidAlert(BidAlertEvent event) {

        BidResult result = event.getResult();
        Auction auction = result.getAuction();

        ReqAlertDto req = ReqAlertDto.builder()
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

        alertClient.createAlert(req);
    }
}
