package com.sidney.banking.outbox;
import jakarta.persistence.*; import java.time.*; import java.util.UUID;
@Entity @Table(name="outbox_events")
public class OutboxEvent {
 @Id private UUID id; @Column(nullable=false) private UUID aggregateId; @Column(nullable=false) private String aggregateType; @Column(nullable=false) private String eventType; @Column(nullable=false,columnDefinition="text") private String payload; @Enumerated(EnumType.STRING) @Column(nullable=false) private OutboxStatus status; private int attempts; @Column(nullable=false) private Instant nextAttemptAt; @Column(nullable=false) private Instant createdAt; private Instant publishedAt; private String lastError;
 protected OutboxEvent(){} public OutboxEvent(UUID id,UUID aggregateId,String eventType,String payload){this.id=id;this.aggregateId=aggregateId;this.aggregateType="Account";this.eventType=eventType;this.payload=payload;this.status=OutboxStatus.PENDING;this.createdAt=this.nextAttemptAt=Instant.now();}
 public void processing(){status=OutboxStatus.PROCESSING;attempts++;} public void completed(){status=OutboxStatus.COMPLETED;publishedAt=Instant.now();lastError=null;} public void failed(Exception e){status=OutboxStatus.FAILED;lastError=e.getMessage()==null?e.getClass().getSimpleName():e.getMessage().substring(0,Math.min(500,e.getMessage().length()));nextAttemptAt=Instant.now().plusSeconds(Math.min(300,(long)Math.pow(2,Math.min(attempts,8))));}
 public UUID getId(){return id;} public UUID getAggregateId(){return aggregateId;} public String getEventType(){return eventType;} public String getPayload(){return payload;}
}
enum OutboxStatus { PENDING, PROCESSING, COMPLETED, FAILED }
