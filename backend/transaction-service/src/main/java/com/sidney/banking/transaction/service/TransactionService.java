package com.sidney.banking.transaction.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidney.banking.transaction.domain.BankTransaction;
import com.sidney.banking.transaction.domain.TransactionStatus;
import com.sidney.banking.transaction.dto.CreateTransactionRequest;
import com.sidney.banking.transaction.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public BankTransaction create(CreateTransactionRequest request) {

        repository.findByIdempotencyKey(request.idempotencyKey())
                .ifPresent(transaction -> {
                    throw new IllegalArgumentException("Idempotency Key já utilizada.");
                });

        BankTransaction transaction = new BankTransaction();

        transaction.setId(UUID.randomUUID());
        transaction.setIdempotencyKey(request.idempotencyKey());

        transaction.setTransactionType(request.transactionType());
        transaction.setTransactionStatus(TransactionStatus.PENDING);

        transaction.setSourceAccountId(request.sourceAccountId());
        transaction.setDestinationAccountId(request.destinationAccountId());

        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());

        transaction.setCreatedAt(OffsetDateTime.now());
        transaction.setUpdatedAt(OffsetDateTime.now());

        return repository.save(transaction);
    }

}