package com.javaauction.payment_service.application.service;

import org.springframework.stereotype.Service;

@Service
public class FeeCalculator {

    private static final double FEE_RATE = 0.15;

    public long calculateTotal(long baseAmount) {
        return baseAmount + calculateFee(baseAmount);
    }

    private long calculateFee(long baseAmount) {
        return Math.round(baseAmount * FEE_RATE);
    }
}
