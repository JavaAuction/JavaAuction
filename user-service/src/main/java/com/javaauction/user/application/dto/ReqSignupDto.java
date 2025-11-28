package com.javaauction.user.application.dto;

import com.javaauction.user.domain.entity.UserEntity;
import com.javaauction.user.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReqSignupDto {
    private String username;
    private String email;
    private String name;
    private String password;
    private String slackId;
    private UserRole role;

    public UserEntity toEntity(){
        return UserEntity.builder()
                .username(username)
                .email(email)
                .name(name)
                .password(password)
                .slackId(slackId)
                .role(role)
                .build();
    }
}
