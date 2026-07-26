CREATE TABLE ledger_accounts (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL UNIQUE,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT ck_ledger_accounts_balance
        CHECK (balance >= 0)
);

CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    transaction_id UUID NOT NULL,
    entry_type VARCHAR(30) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    description VARCHAR(255),
    balance_after NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT ck_ledger_entries_amount
        CHECK (amount > 0),

    CONSTRAINT ck_ledger_entries_type
        CHECK (entry_type IN ('CREDIT', 'DEBIT')),

    CONSTRAINT uk_ledger_entries_transaction_account
        UNIQUE (transaction_id, account_id)
);

CREATE INDEX idx_ledger_entries_account_id
    ON ledger_entries(account_id);

CREATE INDEX idx_ledger_entries_created_at
    ON ledger_entries(created_at);

CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    aggregate_type VARCHAR(100) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT ck_outbox_events_status
        CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED'))
);

CREATE INDEX idx_outbox_events_status
    ON outbox_events(status);

CREATE INDEX idx_outbox_events_created_at
    ON outbox_events(created_at);