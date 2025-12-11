package com.javaauction.payment_service.infrastructure.message.producer;

import com.javaauction.payment_service.infrastructure.message.event.WalletDeductFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.WalletDeductSucceededEvent;

public interface WalletEventProducer {

    void publishWalletDeductSucceeded(WalletDeductSucceededEvent event);

    void publishWalletDeductFailed(WalletDeductFailedEvent event);
}
