package com.javaauction.payment_service.application.service;

import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import com.javaauction.payment_service.domain.repository.WalletTransactionRepository;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import com.javaauction.payment_service.presentation.dto.response.ResGetTransactionDto;
import com.javaauction.payment_service.presentation.dto.response.ResGetTransactionsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.javaauction.payment_service.presentation.advice.PaymentErrorCode.WALLET_TRANSACTION_INVALID_RELATION;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletTransactionServiceV1 {

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
}
