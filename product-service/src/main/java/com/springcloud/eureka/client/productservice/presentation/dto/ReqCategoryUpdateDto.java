package com.springcloud.eureka.client.productservice.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCategoryUpdateDto {
    @NotBlank
    private String name;
}
