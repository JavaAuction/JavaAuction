package com.javaauction.auction_service.infrastructure.client;

import com.javaauction.auction_service.infrastructure.client.dto.RepPostInternalAlertsDtoV1;
import com.javaauction.auction_service.infrastructure.client.dto.ReqPostInternalAlertsDtoV1;
import com.javaauction.global.presentation.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "alert-service")
public interface AlertFeignClient {

    @PostMapping("/internal/v1/alerts")
    ResponseEntity<ApiResponse<RepPostInternalAlertsDtoV1>> createAlert(
        @RequestBody ReqPostInternalAlertsDtoV1 reqDto);
}
