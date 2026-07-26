package com.sidney.banking.ledger.event;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.sidney.banking.ledger.domain.EntryType;

public record LedgerEntryCreatedEvent(
        UUID eventId,
        UUID accountId,
        UUID transactionId,
        EntryType entryType,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String description,
        OffsetDateTime occurredAt
) {
}