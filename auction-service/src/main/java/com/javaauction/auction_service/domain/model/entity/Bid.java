package com.javaauction.auction_service.domain.model.entity;

import com.javaauction.auction_service.domain.model.enums.BidStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_bid")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bid_id", nullable = false)
    private UUID bidId;

    @Column(name = "auction_id", nullable = false)
    private UUID auctionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "bid_price", nullable = false)
    private Long bidPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BidStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Bid(UUID auctionId, Long userId, Long bidPrice) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.bidPrice = bidPrice;
        this.status = BidStatus.HELD;
    }

    public static Bid create(UUID auctionId, Long userId, Long bidPrice) {
        return new Bid(auctionId, userId, bidPrice);
    }
}
