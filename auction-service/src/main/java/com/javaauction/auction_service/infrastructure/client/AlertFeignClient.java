package com.javaauction.auction_service.infrastructure.client;

import com.javaauction.auction_service.infrastructure.client.dto.ReqPostInternalAlertsDtoV1;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "alert-service")
public interface AlertFeignClient {

    @PostMapping("/internal/v1/alerts")
    void createAlert(@RequestBody ReqPostInternalAlertsDtoV1 reqDto);
}
