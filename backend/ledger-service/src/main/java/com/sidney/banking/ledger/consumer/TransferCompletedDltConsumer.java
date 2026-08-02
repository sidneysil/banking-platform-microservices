package com.sidney.banking.ledger.consumer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import com.sidney.banking.ledger.dlt.FailedEvent;
import com.sidney.banking.ledger.dlt.FailedEventRepository;

@Component
public class TransferCompletedDltConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(
                    TransferCompletedDltConsumer.class
            );

    private final FailedEventRepository failedEventRepository;

    public TransferCompletedDltConsumer(
            FailedEventRepository failedEventRepository
    ) {
        this.failedEventRepository = failedEventRepository;
    }

    @KafkaListener(
            topics = "${topics.transfer-completed-dlt}",
            groupId = "ledger-service-dlt",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void consume(
            ConsumerRecord<String, String> record
    ) {
        String originalTopic = readStringHeader(
                record,
                KafkaHeaders.DLT_ORIGINAL_TOPIC,
                record.topic()
        );

        int originalPartition = readIntegerHeader(
                record,
                KafkaHeaders.DLT_ORIGINAL_PARTITION,
                record.partition()
        );

        long originalOffset = readLongHeader(
                record,
                KafkaHeaders.DLT_ORIGINAL_OFFSET,
                record.offset()
        );

        String errorMessage = readStringHeader(
                record,
                KafkaHeaders.DLT_EXCEPTION_MESSAGE,
                "Erro não informado"
        );

        FailedEvent failedEvent = new FailedEvent(
                originalTopic,
                originalPartition,
                originalOffset,
                record.key(),
                record.value(),
                errorMessage
        );

        failedEventRepository.save(failedEvent);

        log.error(
                """
                Evento persistido na tabela failed_events.
                originalTopic={}
                originalPartition={}
                originalOffset={}
                key={}
                error={}
                """,
                originalTopic,
                originalPartition,
                originalOffset,
                record.key(),
                errorMessage
        );
    }

    private String readStringHeader(
            ConsumerRecord<String, String> record,
            String headerName,
            String defaultValue
    ) {
        Header header = record.headers().lastHeader(headerName);

        if (header == null || header.value() == null) {
            return defaultValue;
        }

        return new String(
                header.value(),
                StandardCharsets.UTF_8
        );
    }

    private int readIntegerHeader(
            ConsumerRecord<String, String> record,
            String headerName,
            int defaultValue
    ) {
        Header header = record.headers().lastHeader(headerName);

        if (header == null || header.value() == null) {
            return defaultValue;
        }

        return ByteBuffer.wrap(header.value()).getInt();
    }

    private long readLongHeader(
            ConsumerRecord<String, String> record,
            String headerName,
            long defaultValue
    ) {
        Header header = record.headers().lastHeader(headerName);

        if (header == null || header.value() == null) {
            return defaultValue;
        }

        return ByteBuffer.wrap(header.value()).getLong();
    }
}