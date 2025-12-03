package com.springcloud.eureka.client.productservice.infrastructure.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponse {

    private String username;
    private String email;
    private String name;

}
