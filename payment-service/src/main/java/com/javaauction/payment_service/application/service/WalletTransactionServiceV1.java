package com.javaauction.payment_service.application.service;

import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import com.javaauction.payment_service.domain.repository.WalletRepository;
import com.javaauction.payment_service.domain.repository.WalletTransactionRepository;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import com.javaauction.payment_service.presentation.dto.request.ReqReleaseDto;
import com.javaauction.payment_service.presentation.dto.response.ResGetTransactionDto;
import com.javaauction.payment_service.presentation.dto.response.ResGetTransactionsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.javaauction.payment_service.domain.enums.HoldStatus.*;
import static com.javaauction.payment_service.domain.enums.TransactionType.HOLD;
import static com.javaauction.payment_service.presentation.advice.PaymentErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletTransactionServiceV1 {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public Page<ResGetTransactionsDto> getTransactions(
            UUID walletId, Pageable pageable, List<TransactionType> transactionTypes, Long minAmount, Long maxAmount
    ) {
        Page<WalletTransaction> walletTransactions = walletTransactionRepository.findByWalletId(
                walletId, pageable, transactionTypes, minAmount, maxAmount
        );

        return walletTransactions.map(ResGetTransactionsDto::from);
    }

    public ResGetTransactionDto getTransaction(UUID walletId, UUID transactionId) {

        WalletTransaction walletTransaction = walletTransactionRepository.findById(transactionId);

        if (!walletTransaction.getWalletId().equals(walletId))
            throw new PaymentException(WALLET_TRANSACTION_INVALID_RELATION);

        return ResGetTransactionDto.fromDomain(walletTransaction);
    }

    @Transactional
    public void settleAuction(ReqReleaseDto request) {

        List<WalletTransaction> holds = walletTransactionRepository.findByAuctionIdAndTransactionTypeAndHoldStatus(
                request.getAuctionId(), HOLD, HOLD_ACTIVE
        );

        if (holds.isEmpty())
            throw new PaymentException(WALLET_TRANSACTION_HOLD_NOT_FOUND);

        for (WalletTransaction hold : holds) {
            if (hold.getBidId().equals(request.getWinnerBidId())) {
                WalletTransaction captured = hold.withHoldStatus(HOLD_CAPTURED);
                walletTransactionRepository.save(captured);
                continue;
            }

            Wallet wallet = walletRepository.findById(hold.getWalletId())
                    .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));

            WalletTransaction released = hold.withHoldStatus(HOLD_RELEASED);
            walletTransactionRepository.save(released);

            Wallet refundedBalance = wallet.withBalance(wallet.getBalance() + hold.getAmount());
            walletRepository.save(refundedBalance);
        }
    }
}
