package com.javaauction.user.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ResInternalBidsDto {

    private String userId;
    private List<InternalBidDto> bids;
}
