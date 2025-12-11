package com.javaauction.payment_service.infrastructure.message.producer;

import com.javaauction.payment_service.infrastructure.message.event.WalletCreateFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.WalletCreateSucceededEvent;
import com.javaauction.payment_service.infrastructure.message.event.WalletDeductFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.WalletDeductSucceededEvent;

public interface WalletEventProducer {

    void publishWalletCreateSucceeded(WalletCreateSucceededEvent event);

    void publishWalletCreateFailed(WalletCreateFailedEvent event);

    void publishWalletDeductSucceeded(WalletDeductSucceededEvent event);

    void publishWalletDeductFailed(WalletDeductFailedEvent event);
}
