package com.javaauction.user.infrastructure.external.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGetRequestEvent {
    private String userId;
    private String correlationId;
}


