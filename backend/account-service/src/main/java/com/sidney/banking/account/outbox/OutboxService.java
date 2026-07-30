package com.sidney.banking.account.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidney.banking.account.event.TransferCompletedEvent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class OutboxService {

    private final OutboxRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxService(
            OutboxRepository repository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void publishTransfer(
            UUID sourceAccountId,
            UUID destinationAccountId,
            BigDecimal amount
    ) {
        TransferCompletedEvent event = new TransferCompletedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                sourceAccountId,
                destinationAccountId,
                amount,
                OffsetDateTime.now()
        );

        save(event);
    }

    public void save(TransferCompletedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = new OutboxEvent(
                    "TRANSFER",
                    event.transactionId(),
                    "TRANSFER_COMPLETED_V1",
                    payload
            );

            repository.save(outboxEvent);

        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                    "Erro ao serializar evento da transferência.",
                    ex
            );
        }
    }
}