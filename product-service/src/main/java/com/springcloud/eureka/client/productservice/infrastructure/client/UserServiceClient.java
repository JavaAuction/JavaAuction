package com.springcloud.eureka.client.productservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/v1/users/{username}")
    UserResponse getUser(@PathVariable("username") String username);

}
