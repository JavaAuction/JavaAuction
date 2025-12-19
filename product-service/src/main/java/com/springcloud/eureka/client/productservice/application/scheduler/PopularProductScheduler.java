package com.springcloud.eureka.client.productservice.application.scheduler;

import com.springcloud.eureka.client.productservice.application.service.PopularProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class PopularProductScheduler {

    private final PopularProductService popularProductService;

    /**
     * 인기 상품 목록 주기적 갱신 (1분마다)
     */
    @Scheduled(cron = "${popular-products.refresh-cron}")
    public void refreshPopularProducts() {
        log.info("=== 인기 상품 목록 갱신 시작 ===");
        try {
            popularProductService.refreshPopularProducts();
            log.info("=== 인기 상품 목록 갱신 완료 ===");
        } catch (Exception e) {
            log.error("인기 상품 목록 갱신 실패", e);
        }
    }
}
