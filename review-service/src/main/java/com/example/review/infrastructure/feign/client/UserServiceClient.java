package com.example.review.infrastructure.feign.client;

import com.example.review.infrastructure.feign.dto.ResGetUserIntDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/internal/users/{userId}")
    ResGetUserIntDto getUser(@PathVariable String userId);

    @GetMapping("/internal/users/{userId}/exists")
    boolean existsUser(@PathVariable String userId);
}

