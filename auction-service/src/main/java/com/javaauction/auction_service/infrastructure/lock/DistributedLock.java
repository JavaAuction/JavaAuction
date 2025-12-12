package com.javaauction.auction_service.infrastructure.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 락의 key 로 사용할 SpEL 표현식
     */
    String key();

    /**
     * 락 prefix (도메인 구분용)
     */
    String prefix() default "lock";

    /**
     * 락 획득 대기 시간 (기본 5초)
     */
    long waitTime() default 5L;

    /**
     * 락 점유 시간 (기본 3초)
     */
    long leaseTime() default 3L;

    /**
     * 시간 단위 (기본 SECONDS)
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
