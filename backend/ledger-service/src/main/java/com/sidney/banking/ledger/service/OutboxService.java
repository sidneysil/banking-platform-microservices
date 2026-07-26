package com.sidney.banking.ledger.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sidney.banking.ledger.domain.LedgerEntry;
import com.sidney.banking.ledger.domain.OutboxEvent;
import com.sidney.banking.ledger.event.LedgerEntryCreatedEvent;
import com.sidney.banking.ledger.repository.OutboxEventRepository;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
public class OutboxService {

    private static final String AGGREGATE_TYPE =
            "LEDGER_ACCOUNT";

    private static final String EVENT_TYPE =
            "LEDGER_ENTRY_CREATED";

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    public void createLedgerEntryCreatedEvent(
            LedgerEntry entry
    ) {
        LedgerEntryCreatedEvent event =
                new LedgerEntryCreatedEvent(
                        UUID.randomUUID(),
                        entry.getAccountId(),
                        entry.getTransactionId(),
                        entry.getEntryType(),
                        entry.getAmount(),
                        entry.getBalanceAfter(),
                        entry.getDescription(),
                        OffsetDateTime.now(ZoneOffset.UTC)
                );

        String payload = serialize(event);

        OutboxEvent outboxEvent =
                new OutboxEvent(
                        entry.getAccountId(),
                        AGGREGATE_TYPE,
                        EVENT_TYPE,
                        payload
                );

        outboxEventRepository.save(outboxEvent);
    }

    private String serialize(
            LedgerEntryCreatedEvent event
    ) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "Não foi possível serializar o evento do Ledger.",
                    exception
            );
        }
    }
}