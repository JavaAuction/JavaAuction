package com.javaauction.auction_service.application.event;

import com.javaauction.auction_service.domain.entity.Bid;
import com.javaauction.auction_service.domain.event.OldBidReleaseEvent;
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

        // TODO: 이후에 자금 동결 해제 API 호출
    }
}
