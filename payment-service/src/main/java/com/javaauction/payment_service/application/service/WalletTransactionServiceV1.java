package com.javaauction.payment_service.application.service;

import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import com.javaauction.payment_service.domain.repository.WalletTransactionRepository;
import com.javaauction.payment_service.presentation.dto.response.ResGetTransactionsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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
}
