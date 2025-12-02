package com.javaauction.auction_service.infrastructure.repository;

import com.javaauction.auction_service.presentation.dto.response.internal.InternalBidDto;

import java.util.List;

public interface BidQueryDslRepository {
    List<InternalBidDto> findInternalBidsByUserId(String userId);
}
