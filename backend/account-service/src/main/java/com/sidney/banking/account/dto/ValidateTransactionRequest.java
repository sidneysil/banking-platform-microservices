package com.sidney.banking.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ValidateTransactionRequest(

        @NotNull
        UUID sourceAccountId,

        @NotNull
        UUID destinationAccountId,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount

) {
}