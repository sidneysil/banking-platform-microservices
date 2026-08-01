package com.sidney.banking.ledger.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.sidney.banking.ledger.event.TransferCompletedEvent;
import com.sidney.banking.ledger.service.LedgerService;

@Component
public class TransferCompletedConsumer {

    private final LedgerService ledgerService;

    public TransferCompletedConsumer(
            LedgerService ledgerService
    ) {
        this.ledgerService = ledgerService;
    }

    @KafkaListener(
            topics = "${topics.transfer-completed}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {
                    "spring.json.value.default.type=com.sidney.banking.ledger.event.TransferCompletedEvent"
            }
    )
    public void consume(TransferCompletedEvent event) {

        ledgerService.debit(
                event.sourceAccountId(),
                event.transactionId(),
                event.amount(),
                "Transferência enviada"
        );

        ledgerService.credit(
                event.destinationAccountId(),
                event.transactionId(),
                event.amount(),
                "Transferência recebida"
        );
    }
}