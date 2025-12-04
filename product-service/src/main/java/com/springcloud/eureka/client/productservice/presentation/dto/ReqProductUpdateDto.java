package com.springcloud.eureka.client.productservice.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqProductUpdateDto {
    private String name;
    private String description;
    private String imageUrl;
    private String categoryName;
}
