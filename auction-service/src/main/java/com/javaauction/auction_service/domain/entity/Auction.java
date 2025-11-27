package com.javaauction.auction_service.domain.entity;

import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import com.javaauction.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "p_auction")
public class Auction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "auction_id", nullable = false)
    private UUID auctionId;

    @Column(name = "productId", nullable = false)
    private UUID productId;

    @Column(name = "successful_bidder")
    private String successfulBidder;

    @Column(name = "start_price", nullable = false)
    private Long startPrice;

    @Column(name = "current_price")
    private Long currentPrice;

    @Column(name = "unit", nullable = false)
    private Long unit;

    @Column(name = "buy_now_enable", nullable = false)
    private Boolean buyNowEnable;

    @Column(name = "buy_now_price")
    private Long buyNowPrice;

    @Column(name = "ended_at", nullable = false)
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AuctionStatus status;

    public void update(
        String successfulBidder,
        Long startPrice,
        Long unit,
        Boolean buyNowEnable,
        Long buyNowPrice,
        LocalDateTime endedAt
    ) {
        this.successfulBidder = successfulBidder;
        this.startPrice = startPrice;
        this.unit = unit;
        this.buyNowEnable = buyNowEnable;
        this.buyNowPrice = buyNowPrice;
        this.endedAt = endedAt;
    }

    public void updateStatus(AuctionStatus status) {
        this.status = status;
    }

    public void reRegister() {
        this.status = AuctionStatus.IN_PROGRESS;
    }

    // 따로 쓸지 같이 쓸지 나중에 정해서 함수 지울 예정
    public void updateSuccessfulBidder(String user) {
        this.successfulBidder = user;
    }

    public void updateCurrentPrice(Long currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void updateCurrentBid(String user, Long currentPrice) {
        this.currentPrice = currentPrice;
        this.successfulBidder = user;
    }


}
