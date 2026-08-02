package com.sidney.banking.ledger.dlt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FailedEventRepository
        extends JpaRepository<FailedEvent, UUID> {
}
