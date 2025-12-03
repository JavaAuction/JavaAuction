package com.javaauction.alertservice.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepGetInternalUsersDtoV1 {
    private String username;
    private String email;
    private String address;
    private String slackId;
    private String role;
}
