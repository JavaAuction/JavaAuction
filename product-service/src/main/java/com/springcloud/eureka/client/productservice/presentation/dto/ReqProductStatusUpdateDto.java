package com.springcloud.eureka.client.productservice.presentation.dto;

import com.springcloud.eureka.client.productservice.domain.enums.ProductStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqProductStatusUpdateDto {
    private ProductStatus productStatus;
    private Long finalPrice;
}
