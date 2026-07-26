package com.sidney.banking.ledger.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidney.banking.ledger.domain.EntryType;
import com.sidney.banking.ledger.domain.LedgerAccount;
import com.sidney.banking.ledger.domain.LedgerEntry;
import com.sidney.banking.ledger.repository.LedgerAccountRepository;
import com.sidney.banking.ledger.repository.LedgerEntryRepository;

@Service
public class LedgerService {

    private final LedgerAccountRepository accountRepository;
    private final LedgerEntryRepository entryRepository;
    private final OutboxService outboxService;

    public LedgerService(
            LedgerAccountRepository accountRepository,
            LedgerEntryRepository entryRepository,
            OutboxService outboxService
    ) {
        this.accountRepository = accountRepository;
        this.entryRepository = entryRepository;
        this.outboxService = outboxService;
    }

    @Transactional
    public LedgerAccount createAccount(UUID accountId) {

        if (accountId == null) {
            throw new IllegalArgumentException(
                    "O identificador da conta é obrigatório."
            );
        }

        return accountRepository
                .findByAccountId(accountId)
                .orElseGet(() ->
                        accountRepository.save(
                                new LedgerAccount(accountId)
                        )
                );
    }

    @Transactional
    public void credit(
            UUID accountId,
            UUID transactionId,
            BigDecimal amount,
            String description
    ) {
        processEntry(
                accountId,
                transactionId,
                EntryType.CREDIT,
                amount,
                description
        );
    }

    @Transactional
    public void debit(
            UUID accountId,
            UUID transactionId,
            BigDecimal amount,
            String description
    ) {
        processEntry(
                accountId,
                transactionId,
                EntryType.DEBIT,
                amount,
                description
        );
    }

    private void processEntry(
            UUID accountId,
            UUID transactionId,
            EntryType entryType,
            BigDecimal amount,
            String description
    ) {
        validateEntryData(
                accountId,
                transactionId,
                amount
        );

        boolean alreadyProcessed =
                entryRepository.existsByTransactionIdAndAccountId(
                        transactionId,
                        accountId
                );

        if (alreadyProcessed) {
            return;
        }

        LedgerAccount account = accountRepository
                .findByAccountId(accountId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Conta contábil não encontrada."
                        )
                );

        if (entryType == EntryType.CREDIT) {
            account.credit(amount);
        } else {
            account.debit(amount);
        }

        LedgerEntry entry = new LedgerEntry(
                accountId,
                transactionId,
                entryType,
                amount,
                description,
                account.getBalance()
        );

        accountRepository.save(account);

        LedgerEntry savedEntry =
                entryRepository.save(entry);

        outboxService.createLedgerEntryCreatedEvent(savedEntry);
    }

    private void validateEntryData(
            UUID accountId,
            UUID transactionId,
            BigDecimal amount
    ) {
        if (accountId == null) {
            throw new IllegalArgumentException(
                    "O identificador da conta é obrigatório."
            );
        }

        if (transactionId == null) {
            throw new IllegalArgumentException(
                    "O identificador da transação é obrigatório."
            );
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "O valor da movimentação deve ser maior que zero."
            );
        }
    }
}