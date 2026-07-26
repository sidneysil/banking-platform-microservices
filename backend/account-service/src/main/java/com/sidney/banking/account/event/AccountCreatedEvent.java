package com.sidney.banking.account.event;

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