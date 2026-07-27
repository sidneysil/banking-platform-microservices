package com.sidney.banking.transaction.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sidney.banking.transaction.domain.BankTransaction;

public interface TransactionRepository extends JpaRepository<BankTransaction, UUID> {

    Optional<BankTransaction> findByIdempotencyKey(String idempotencyKey);

}