package com.example.review.infrastructure.event;

import com.example.review.infrastructure.feign.dto.ResGetUserIntDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGetResponseEvent {
    private String correlationId;
    private ResGetUserIntDto user;
}


