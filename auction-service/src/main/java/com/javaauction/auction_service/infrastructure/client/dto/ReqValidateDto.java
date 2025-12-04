package com.javaauction.auction_service.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqValidateDto {
    private String userId;
    private Long bidPrice;
}

