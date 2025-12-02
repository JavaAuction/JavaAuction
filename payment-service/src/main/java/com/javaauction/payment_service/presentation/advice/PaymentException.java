package com.javaauction.payment_service.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import com.javaauction.global.presentation.exception.BussinessException;

public class PaymentException extends BussinessException {
    public PaymentException(ResponseCode responseCode) {
        super(responseCode);
    }
}
