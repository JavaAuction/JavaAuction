package com.javaauction.payment_service.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "지갑 삭제 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqDeleteDto {

    @Schema(description = "사용자 ID", example = "isaac6")
    private String userId;
}
