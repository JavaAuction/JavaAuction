package com.javaauction.auction_service.presentation.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqPostBidDto {
    private Long bidPrice;
}
