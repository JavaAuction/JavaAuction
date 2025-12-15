package com.example.review.infrastructure.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionValidationResponseEvent {
    private String correlationId;
    private boolean isValid;
    private String sellerId;
    private String buyerId;
}

