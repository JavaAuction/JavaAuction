package com.javaauction.payment_service.presentation.dto.request;

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
