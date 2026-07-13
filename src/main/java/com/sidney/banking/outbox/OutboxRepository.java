package com.sidney.banking.outbox;
import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.time.Instant; import java.util.*;
public interface OutboxRepository extends JpaRepository<OutboxEvent,UUID>{ @Query(value="select * from outbox_events where status in ('PENDING','FAILED') and next_attempt_at<=:now order by created_at limit 50 for update skip locked",nativeQuery=true) List<OutboxEvent> lockBatch(@Param("now") Instant now); }
