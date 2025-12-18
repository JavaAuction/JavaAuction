package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.model.Wallet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Schema(description = "지갑 생성 응답 DTO")
@Getter
@Builder
public class ResCreateDto {

    @Schema(description = "지갑 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID walletId;

    @Schema(description = "사용자 ID", example = "isaac6")
    private String userId;

    @Schema(description = "보유 잔액", defaultValue = "0")
    private Long balance;

    public static ResCreateDto from(Wallet wallet) {
        return ResCreateDto.builder()
                .walletId(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .build();
    }
}
