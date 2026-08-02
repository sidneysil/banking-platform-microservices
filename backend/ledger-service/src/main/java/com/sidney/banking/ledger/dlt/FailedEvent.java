package com.sidney.banking.ledger.dlt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "failed_events")
public class FailedEvent {

    @Id
    private UUID id;

    @Column(name = "original_topic", nullable = false)
    private String originalTopic;

    @Column(name = "partition_number", nullable = false)
    private Integer partition;

    @Column(name = "original_offset", nullable = false)
    private Long offset;

    @Column(name = "message_key")
    private String messageKey;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected FailedEvent() {
    }

    public FailedEvent(
            String originalTopic,
            Integer partition,
            Long offset,
            String messageKey,
            String payload,
            String errorMessage
    ) {
        this.id = UUID.randomUUID();
        this.originalTopic = originalTopic;
        this.partition = partition;
        this.offset = offset;
        this.messageKey = messageKey;
        this.payload = payload;
        this.errorMessage = errorMessage;
    }

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (createdAt == null) {
            createdAt = OffsetDateTime.now(ZoneOffset.UTC);
        }
    }

    public UUID getId() {
        return id;
    }

    public String getOriginalTopic() {
        return originalTopic;
    }

    public Integer getPartition() {
        return partition;
    }

    public Long getOffset() {
        return offset;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getPayload() {
        return payload;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}