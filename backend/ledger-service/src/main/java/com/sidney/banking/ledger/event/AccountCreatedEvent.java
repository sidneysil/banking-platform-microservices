package com.sidney.banking.ledger.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountCreatedEvent(
        UUID eventId,
        UUID accountId,
        UUID customerId,
        String accountType,
        OffsetDateTime occurredAt
) {
}