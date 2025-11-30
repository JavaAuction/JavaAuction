package com.javaauction.auction_service.presentation.dto.response.internal;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResInternalBidsDto {

    private Long userId;
    private List<InternalBidDto> bids;
}
