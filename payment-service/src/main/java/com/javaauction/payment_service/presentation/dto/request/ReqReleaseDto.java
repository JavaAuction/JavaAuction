package com.javaauction.payment_service.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqReleaseDto {

    @NotNull
    private UUID auctionId;

    @NotNull
    private UUID winnerBidId;
}
