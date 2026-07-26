package com.sidney.banking.account.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidney.banking.account.api.AccountResponse;
import com.sidney.banking.account.api.CreateAccountRequest;
import com.sidney.banking.account.domain.Account;
import com.sidney.banking.account.event.AccountCreatedEvent;
import com.sidney.banking.account.event.AccountEventPublisher;
import com.sidney.banking.account.repository.AccountRepository;

@Service
public class AccountService {

    private static final String DEFAULT_AGENCY = "0001";

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AccountEventPublisher accountEventPublisher;

    public AccountService(
            AccountRepository accountRepository,
            AccountNumberGenerator accountNumberGenerator,
            AccountEventPublisher accountEventPublisher
    ) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.accountEventPublisher = accountEventPublisher;
    }

    @Transactional
    public AccountResponse create(
            UUID customerId,
            CreateAccountRequest request
    ) {

        boolean accountAlreadyExists =
                accountRepository.existsByCustomerIdAndType(
                        customerId,
                        request.type()
                );

        if (accountAlreadyExists) {
            throw new IllegalArgumentException(
                    "O cliente já possui uma conta desse tipo."
            );
        }

        String accountNumber = generateUniqueAccountNumber();

        Account account = new Account(
                customerId,
                DEFAULT_AGENCY,
                accountNumber,
                request.type()
        );

        Account savedAccount = accountRepository.save(account);

        AccountCreatedEvent event = new AccountCreatedEvent(
                UUID.randomUUID(),
                savedAccount.getId(),
                savedAccount.getCustomerId(),
                savedAccount.getType().name(),
                savedAccount.getCreatedAt()
        );

        accountEventPublisher.publish(event);

        return AccountResponse.from(savedAccount);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findByCustomerId(UUID customerId) {

        return accountRepository
                .findByCustomerId(customerId)
                .stream()
                .map(AccountResponse::from)
                .toList();
    }

    private String generateUniqueAccountNumber() {

        String accountNumber;

        do {
            accountNumber = accountNumberGenerator.generate();
        } while (
                accountRepository.existsByAccountNumber(accountNumber)
        );

        return accountNumber;
    }
}