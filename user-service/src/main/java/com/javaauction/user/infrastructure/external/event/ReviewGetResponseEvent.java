package com.javaauction.user.infrastructure.external.event;

import com.javaauction.user.infrastructure.external.dto.GetReviewIntDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewGetResponseEvent {
    private String correlationId;
    private List<GetReviewIntDto> reviews;
}

