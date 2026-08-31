package com.sidney.banking.transaction.client;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountTransferRequest(
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount
) {
}