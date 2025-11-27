package com.javaauction.auction_service.presentation.dto.response;

import com.javaauction.auction_service.domain.model.enums.BidStatus;
import lombok.AllArgsConstructor; import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ResPostBidDto {
    private UUID bidId;
    private UUID productId;
    private Long bidPrice;
    private BidStatus bidStatus;
    private LocalDateTime createdAt;
}
