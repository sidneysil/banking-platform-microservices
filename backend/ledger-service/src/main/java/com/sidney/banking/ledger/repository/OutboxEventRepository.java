package com.sidney.banking.ledger.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sidney.banking.ledger.domain.OutboxEvent;
import com.sidney.banking.ledger.domain.OutboxStatus;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(
            OutboxStatus status,
            Pageable pageable
    );
}
