package com.sidney.banking.ledger.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "ledger_entries",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ledger_entries_transaction_account",
                        columnNames = {
                                "transaction_id",
                                "account_id"
                        }
                )
        }
)
public class LedgerEntry {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "account_id", nullable = false, updatable = false)
    private UUID accountId;

    @Column(name = "transaction_id", nullable = false, updatable = false)
    private UUID transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 30, updatable = false)
    private EntryType entryType;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2,
            updatable = false
    )
    private BigDecimal amount;

    @Column(name = "description", length = 255, updatable = false)
    private String description;

    @Column(
            name = "balance_after",
            nullable = false,
            precision = 19,
            scale = 2,
            updatable = false
    )
    private BigDecimal balanceAfter;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected LedgerEntry() {
    }

    public LedgerEntry(
            UUID accountId,
            UUID transactionId,
            EntryType entryType,
            BigDecimal amount,
            String description,
            BigDecimal balanceAfter
    ) {
        this.id = UUID.randomUUID();
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.entryType = entryType;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
    }

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (createdAt == null) {
            createdAt = OffsetDateTime.now(ZoneOffset.UTC);
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
