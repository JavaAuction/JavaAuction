package com.javaauction.user.infrastructure.external.event;

import com.javaauction.user.presentation.dto.ResGetUserIntDto;
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


