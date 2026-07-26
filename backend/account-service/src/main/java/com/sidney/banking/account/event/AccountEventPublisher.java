package com.sidney.banking.account.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class AccountEventPublisher {

    private final KafkaTemplate<String, AccountCreatedEvent> kafkaTemplate;
    private final String topic;

    public AccountEventPublisher(
            KafkaTemplate<String, AccountCreatedEvent> kafkaTemplate,
            @Value("${topics.account-created}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(AccountCreatedEvent event) {

        if (TransactionSynchronizationManager.isActualTransactionActive()) {

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {

                        @Override
                        public void afterCommit() {
                            send(event);
                        }
                    }
            );

            return;
        }

        send(event);
    }

    private void send(AccountCreatedEvent event) {

        kafkaTemplate.send(
                topic,
                event.accountId().toString(),
                event
        );
    }
}