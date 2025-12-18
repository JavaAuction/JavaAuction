package com.javaauction.payment_service.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "현금 출금 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqWithdrawDto {

    @Schema(description = "출금 요청 금액", example = "300000")
    @NotNull
    @Min(1)
    private Long withdrawAmount;
}
