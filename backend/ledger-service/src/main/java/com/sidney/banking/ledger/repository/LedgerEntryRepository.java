package com.sidney.banking.ledger.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sidney.banking.ledger.domain.LedgerEntry;

public interface LedgerEntryRepository
        extends JpaRepository<LedgerEntry, UUID> {

    List<LedgerEntry> findByAccountIdOrderByCreatedAtDesc(UUID accountId);

    boolean existsByTransactionIdAndAccountId(
            UUID transactionId,
            UUID accountId
    );
}
