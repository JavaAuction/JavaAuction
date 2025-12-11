package com.javaauction.user.infrastructure.external.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingGetResponseEvent {
    private String correlationId;
    private double rating;
}