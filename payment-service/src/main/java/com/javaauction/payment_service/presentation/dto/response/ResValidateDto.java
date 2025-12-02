package com.javaauction.payment_service.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResValidateDto {

    private Boolean valid;
    private String reasonCode;
    private String reasonMessage;
    private Long requiredAmount;
    private Long currentBalance;
}
