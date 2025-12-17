package com.springcloud.eureka.client.productservice.infrastructure.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "popular-products")
public class PopularProductConfig {
    private int topCount = 10;
    private int cacheTtlMinutes = 60;
    private String refreshCron = "0 */10 * * * *";
}