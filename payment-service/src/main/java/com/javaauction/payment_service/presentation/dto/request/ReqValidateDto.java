package com.javaauction.payment_service.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "잔액 검증 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqValidateDto {

    @Schema(description = "사용자 ID", example = "isaac6")
    private String userId;

    @Schema(description = "입찰금 금액", example = "360000")
    private Long bidPrice;
}
