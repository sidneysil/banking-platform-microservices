package com.sidney.banking.outbox;
import org.springframework.beans.factory.annotation.Value; import org.springframework.kafka.core.KafkaTemplate; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional; import java.time.Instant;
@Component
public class OutboxPublisher {
 private final OutboxRepository repository; private final KafkaTemplate<String,String> kafka; private final String topic;
 public OutboxPublisher(OutboxRepository r,KafkaTemplate<String,String> k,@Value("${banking.kafka.topic}") String t){repository=r;kafka=k;topic=t;}
 @Scheduled(fixedDelay=1000) @Transactional public void publish(){for(var event:repository.lockBatch(Instant.now())){try{event.processing();kafka.send(topic,event.getAggregateId().toString(),event.getPayload()).get();event.completed();}catch(Exception e){event.failed(e);}}}
}
