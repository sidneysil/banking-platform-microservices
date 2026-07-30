package com.sidney.banking.account.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxPublisher {

    private static final String TOPIC = "banking.transfer.completed.v1";

    private final OutboxRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(
            OutboxRepository repository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay:5000}")
    @Transactional
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                repository.findTop100ByStatusOrderByCreatedAtAsc(
                        OutboxStatus.PENDING
                );

        for (OutboxEvent event : events) {

            try {

                kafkaTemplate.send(
                        TOPIC,
                        event.getAggregateId().toString(),
                        event.getPayload()
                ).get();

                event.markAsPublished();

                repository.save(event);

            } catch (Exception ex) {

                event.markAsFailed();

                repository.save(event);

            }
        }
    }
}