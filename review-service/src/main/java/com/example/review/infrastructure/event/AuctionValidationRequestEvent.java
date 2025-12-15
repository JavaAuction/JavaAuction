package com.example.review.infrastructure.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionValidationRequestEvent {
    private UUID auctionId;
    private String userId;
    private String correlationId;
}

