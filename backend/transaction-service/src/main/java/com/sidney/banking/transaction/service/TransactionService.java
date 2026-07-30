package com.sidney.banking.transaction.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidney.banking.transaction.domain.BankTransaction;
import com.sidney.banking.transaction.domain.TransactionStatus;
import com.sidney.banking.transaction.domain.TransactionType;
import com.sidney.banking.transaction.dto.CreateTransactionRequest;
import com.sidney.banking.transaction.dto.TransactionResponse;
import com.sidney.banking.transaction.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TransactionResponse create(CreateTransactionRequest request) {

        validateRequest(request);

        BankTransaction existingTransaction = repository
                .findByIdempotencyKey(request.idempotencyKey())
                .orElse(null);

        if (existingTransaction != null) {
            return toResponse(existingTransaction);
        }

        OffsetDateTime now = OffsetDateTime.now();

        BankTransaction transaction = new BankTransaction();

        transaction.setId(UUID.randomUUID());
        transaction.setIdempotencyKey(request.idempotencyKey().trim());
        transaction.setTransactionType(request.transactionType());
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transaction.setSourceAccountId(request.sourceAccountId());
        transaction.setDestinationAccountId(request.destinationAccountId());
        transaction.setAmount(request.amount());
        transaction.setDescription(normalizeDescription(request.description()));
        transaction.setFailureReason(null);
        transaction.setCreatedAt(now);
        transaction.setUpdatedAt(now);

        BankTransaction savedTransaction = repository.save(transaction);

        return toResponse(savedTransaction);
    }

    private void validateRequest(CreateTransactionRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Os dados da transação são obrigatórios.");
        }

        if (request.idempotencyKey() == null
                || request.idempotencyKey().isBlank()) {

            throw new IllegalArgumentException(
                    "A chave de idempotência é obrigatória.");
        }

        if (request.idempotencyKey().length() > 100) {
            throw new IllegalArgumentException(
                    "A chave de idempotência deve possuir no máximo 100 caracteres.");
        }

        if (request.transactionType() == null) {
            throw new IllegalArgumentException(
                    "O tipo da transação é obrigatório.");
        }

        if (request.amount() == null) {
            throw new IllegalArgumentException(
                    "O valor da transação é obrigatório.");
        }

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O valor da transação deve ser maior que zero.");
        }

        validateAccounts(request);
    }

    private void validateAccounts(CreateTransactionRequest request) {

        TransactionType transactionType = request.transactionType();

        switch (transactionType) {

            case CREDIT -> {
                if (request.destinationAccountId() == null) {
                    throw new IllegalArgumentException(
                            "A conta de destino é obrigatória para crédito.");
                }
            }

            case DEBIT -> {
                if (request.sourceAccountId() == null) {
                    throw new IllegalArgumentException(
                            "A conta de origem é obrigatória para débito.");
                }
            }

            case TRANSFER, PIX -> {
                if (request.sourceAccountId() == null) {
                    throw new IllegalArgumentException(
                            "A conta de origem é obrigatória.");
                }

                if (request.destinationAccountId() == null) {
                    throw new IllegalArgumentException(
                            "A conta de destino é obrigatória.");
                }

                if (request.sourceAccountId()
                        .equals(request.destinationAccountId())) {

                    throw new IllegalArgumentException(
                            "A conta de origem e a conta de destino devem ser diferentes.");
                }
            }
        }
    }

    private String normalizeDescription(String description) {

        if (description == null || description.isBlank()) {
            return null;
        }

        String normalizedDescription = description.trim();

        if (normalizedDescription.length() > 255) {
            throw new IllegalArgumentException(
                    "A descrição deve possuir no máximo 255 caracteres.");
        }

        return normalizedDescription;
    }

    private TransactionResponse toResponse(
            BankTransaction transaction) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getIdempotencyKey(),
                transaction.getTransactionType(),
                transaction.getTransactionStatus(),
                transaction.getSourceAccountId(),
                transaction.getDestinationAccountId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getFailureReason(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}