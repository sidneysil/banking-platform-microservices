package com.sidney.banking.transaction.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.sidney.banking.transaction.domain.TransactionStatus;
import com.sidney.banking.transaction.domain.TransactionType;

public record TransactionResponse(


		UUID id,

        String idempotencyKey,

        TransactionType transactionType,

        TransactionStatus transactionStatus,

        UUID sourceAccountId,

        UUID destinationAccountId,

        BigDecimal amount,

        String description,

        String failureReason,

        OffsetDateTime createdAt,

        OffsetDateTime updatedAt


) {
}
