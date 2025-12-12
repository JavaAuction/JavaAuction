package com.javaauction.auction_service.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        String address = "redis://" + redisHost + ":" + redisPort;
        log.info("[RedissonConfig] Connecting to Redis: {}", address);

        Config config = new Config();
        config.useSingleServer()
                .setAddress(address);

        return Redisson.create(config);
    }
}

