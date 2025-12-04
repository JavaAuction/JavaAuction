package com.javaauction.user.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetReviewIntDto {
    private String writer;
    private String target;
    private double rating;
    private String content;
}
