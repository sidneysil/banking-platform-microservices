package com.sidney.banking.ledger.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sidney.banking.ledger.domain.LedgerAccount;

public interface LedgerAccountRepository
        extends JpaRepository<LedgerAccount, UUID> {

    Optional<LedgerAccount> findByAccountId(UUID accountId);

    boolean existsByAccountId(UUID accountId);
}