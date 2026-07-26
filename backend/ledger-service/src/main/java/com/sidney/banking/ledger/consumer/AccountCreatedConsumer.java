package com.sidney.banking.ledger.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.sidney.banking.ledger.event.AccountCreatedEvent;
import com.sidney.banking.ledger.service.LedgerService;

@Component
public class AccountCreatedConsumer {

    private final LedgerService ledgerService;

    public AccountCreatedConsumer(
            LedgerService ledgerService
    ) {
        this.ledgerService = ledgerService;
    }

    @KafkaListener(
            topics = "${topics.account-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(
            AccountCreatedEvent event
    ) {
        ledgerService.createAccount(
                event.accountId()
        );
    }
}
