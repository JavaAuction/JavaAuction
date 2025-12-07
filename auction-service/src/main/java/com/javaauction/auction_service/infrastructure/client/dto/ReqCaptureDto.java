package com.javaauction.auction_service.infrastructure.client.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqCaptureDto {

    @NotNull
    private UUID auctionId;
}

