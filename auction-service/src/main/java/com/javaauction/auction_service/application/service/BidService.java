package com.javaauction.auction_service.application.service;

import com.javaauction.auction_service.domain.event.BidResult;
import com.javaauction.auction_service.domain.event.OldBidReleaseEvent;
import com.javaauction.auction_service.domain.service.BidDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidDomainService bidDomainService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 입찰 처리 서비스
     */
    @Transactional
    public BidResult placeBid(UUID auctionId, String userId, Long bidPrice) {

        // TODO: 결제 precheck (결제 서비스 완성 후 진행)
        // precheckFeignClient

        BidResult result = bidDomainService.placeBidWithLock(
                auctionId,
                userId,
                bidPrice
        );

        // 이전 최고 입찰자 상태 RELEASE로 변경하는 이벤트
        eventPublisher.publishEvent(new OldBidReleaseEvent(result));

        return result;
    }
}
