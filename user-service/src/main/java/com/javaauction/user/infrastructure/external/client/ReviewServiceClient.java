package com.javaauction.user.infrastructure.external.client;

import com.javaauction.user.infrastructure.external.dto.GetReviewIntDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "review-service")
public interface ReviewServiceClient {
    @GetMapping("/internal/reviews/user/{userId}")
    List<GetReviewIntDto> getReviewByUser(@PathVariable String userId);

    @GetMapping("/internal/reviews/user/{userId}/rating")
    double getUserRating(@PathVariable String userId);
}
