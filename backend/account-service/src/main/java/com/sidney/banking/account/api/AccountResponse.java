package com.sidney.banking.account.api;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.sidney.banking.account.domain.Account;
import com.sidney.banking.account.domain.AccountStatus;
import com.sidney.banking.account.domain.AccountType;

public record AccountResponse(

        UUID id,
        UUID customerId,
        String agency,
        String accountNumber,
        AccountType type,
        AccountStatus status,
        BigDecimal balance,
        OffsetDateTime createdAt

) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getCustomerId(),
                account.getAgency(),
                account.getAccountNumber(),
                account.getType(),
                account.getStatus(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
}