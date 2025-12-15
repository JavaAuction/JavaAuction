package com.javaauction.auction_service.domain.entity;

import com.javaauction.auction_service.domain.entity.enums.BidStatus;
import com.javaauction.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_bid")
public class Bid extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bid_id", nullable = false)
    private UUID bidId;

    @Column(name = "auction_id", nullable = false)
    private UUID auctionId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "bid_price", nullable = false)
    private Long bidPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BidStatus status;

    private Bid(UUID auctionId, String userId, Long bidPrice) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.bidPrice = bidPrice;
        this.status = BidStatus.PENDING;
    }

    public static Bid create(UUID auctionId, String userId, Long bidPrice) {
        return new Bid(auctionId, userId, bidPrice);
    }

    public void release() {
        this.status = BidStatus.RELEASED;
    }
    public boolean isPending() {
        return this.status == BidStatus.PENDING;
    }

    public boolean isHeld() {
        return this.status == BidStatus.HELD;
    }

    public boolean isFailed() {
        return this.status == BidStatus.FAILED;
    }

    public void markHeld() {
        this.status = BidStatus.HELD;
    }

    public void markReleased() {
        this.status = BidStatus.RELEASED;
    }

    public void markFailed() {
        this.status = BidStatus.FAILED;
    }

}
