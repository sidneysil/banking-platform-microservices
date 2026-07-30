package com.sidney.banking.account.event;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransferCompletedEvent(
        UUID eventId,
        UUID transactionId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        OffsetDateTime occurredAt
) {
}