package com.javaauction.alertservice.application.client;

import com.javaauction.alertservice.presentation.dto.response.RepGetInternalUsersDtoV1;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClientV1 {
    @GetMapping("/internal/users/{userId}")
    RepGetInternalUsersDtoV1 getUser(@PathVariable("userId") String userId);

}
