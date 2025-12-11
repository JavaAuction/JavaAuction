package com.javaauction.payment_service.infrastructure.message.producer;

import com.javaauction.payment_service.infrastructure.message.event.fail.WalletCreateFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.fail.WalletSettleFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.success.WalletCreateSucceededEvent;
import com.javaauction.payment_service.infrastructure.message.event.fail.WalletDeductFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.success.WalletDeductSucceededEvent;
import com.javaauction.payment_service.infrastructure.message.event.success.WalletSettleSucceededEvent;

public interface WalletEventProducer {

    void publishWalletCreateSucceeded(WalletCreateSucceededEvent event);

    void publishWalletCreateFailed(WalletCreateFailedEvent event);

    void publishWalletDeductSucceeded(WalletDeductSucceededEvent event);

    void publishWalletDeductFailed(WalletDeductFailedEvent event);

    void publishWalletSettleSucceeded(WalletSettleSucceededEvent event);

    void publishWalletSettleFailed(WalletSettleFailedEvent event);
}
