package com.sidney.banking.audit;
import com.fasterxml.jackson.databind.*; import jakarta.persistence.*; import org.springframework.kafka.annotation.KafkaListener; import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional; import java.time.Instant; import java.util.UUID;
@Component
public class AuditConsumer {
 @PersistenceContext private EntityManager em; private final ObjectMapper json; public AuditConsumer(ObjectMapper j){json=j;}
 @KafkaListener(topics="${banking.kafka.topic}") @Transactional public void consume(String payload) throws Exception {var node=json.readTree(payload);var eventId=UUID.fromString(node.required("eventId").asText());if(em.find(ProcessedEvent.class,eventId)!=null)return;em.persist(new ProcessedEvent(eventId,"audit-service"));em.persist(new AuditEvent(eventId,UUID.fromString(node.required("accountId").asText()),"AccountBalanceChanged",payload));}
}
@Entity @Table(name="processed_events") class ProcessedEvent{@Id UUID eventId;String consumerName;Instant processedAt;protected ProcessedEvent(){}ProcessedEvent(UUID id,String name){eventId=id;consumerName=name;processedAt=Instant.now();}}
@Entity @Table(name="audit_events") class AuditEvent{@Id UUID id;@Column(unique=true)UUID eventId;UUID aggregateId;String eventType;@Column(columnDefinition="text")String payload;Instant receivedAt;protected AuditEvent(){}AuditEvent(UUID e,UUID a,String t,String p){id=UUID.randomUUID();eventId=e;aggregateId=a;eventType=t;payload=p;receivedAt=Instant.now();}}
