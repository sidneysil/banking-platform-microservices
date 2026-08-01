package com.sidney.banking.ledger.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransferCompletedDltConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(
                    TransferCompletedDltConsumer.class
            );

    @KafkaListener(
            topics = "${topics.transfer-completed-dlt}",
            groupId = "ledger-service-dlt",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void consume(
            ConsumerRecord<String, String> record
    ) {
        log.error(
                """
                Evento enviado para DLT.
                topic={}
                partition={}
                offset={}
                key={}
                payload={}
                """,
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value()
        );
    }
}