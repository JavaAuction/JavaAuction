package com.example.review.infrastructure.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserExistsResponseEvent {
    private String correlationId;
    private boolean exists;
}


