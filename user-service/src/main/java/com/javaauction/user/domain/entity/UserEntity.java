package com.javaauction.user.domain.entity;

import com.javaauction.global.infrastructure.entity.BaseEntity;
import com.javaauction.user.application.dto.ReqUpdateDto;
import com.javaauction.user.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {
    @Id
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String slackId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    private UUID address;

    public void update(ReqUpdateDto updateRequestDto) {
        if (updateRequestDto.getEmail() != null && !updateRequestDto.getEmail().isEmpty()) {
            this.email = updateRequestDto.getEmail();
        }
        if (updateRequestDto.getName() != null && !updateRequestDto.getName().isEmpty()) {
            this.name = updateRequestDto.getName();
        }
        if (updateRequestDto.getPassword() != null && !updateRequestDto.getPassword().isEmpty()) {
            this.password = updateRequestDto.getPassword();
        }
    }
}
