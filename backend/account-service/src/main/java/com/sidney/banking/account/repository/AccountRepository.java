package com.sidney.banking.account.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sidney.banking.account.domain.Account;
import com.sidney.banking.account.domain.AccountType;

public interface AccountRepository
        extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(UUID customerId);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByCustomerIdAndType(
            UUID customerId,
            AccountType type
    );
}