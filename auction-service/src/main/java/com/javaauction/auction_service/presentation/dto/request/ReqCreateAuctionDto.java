package com.javaauction.auction_service.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReqCreateAuctionDto(
    @NotNull(message = "상품 아이디를 입력해주세요.")
    UUID productId,
    @Min(value = 10, message = "경매 시작가는 10원 이상이어야 합니다.")
    Long startPrice,
    @Min(value = 10, message = "입찰 단위는 10원 이상이어야 합니다.")
    Long unit,
    Boolean buyNowEnable,
    Long buyNowPrice,
    @NotNull(message = "경매 종료 시간을 입력해주세요.")
    LocalDateTime endAt
) {

}
