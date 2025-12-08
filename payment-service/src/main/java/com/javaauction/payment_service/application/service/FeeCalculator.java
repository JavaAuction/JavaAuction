package com.javaauction.payment_service.application.service;

import org.springframework.stereotype.Service;

@Service
public class FeeCalculator {

    private static final double FEE_RATE = 0.15;

    public long calculateFee(long baseAmount) {
        return Math.round(baseAmount * FEE_RATE);
    }

    public long calculateNetAmount(long baseAmount) {
        return baseAmount - calculateFee(baseAmount);
    }
}
