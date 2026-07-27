package com.sidney.banking.transaction.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.sidney.banking.transaction.domain.TransactionType;

public record CreateTransactionRequest(

        UUID sourceAccountId,

        UUID destinationAccountId,

        BigDecimal amount,

        TransactionType transactionType,

        String description,

        String idempotencyKey

) {
}
