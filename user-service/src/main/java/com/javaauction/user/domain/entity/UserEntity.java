package com.javaauction.user.domain.entity;

import com.javaauction.global.infrastructure.entity.BaseEntity;
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
}
