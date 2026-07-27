CREATE TABLE bank_transactions (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(100) NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    transaction_status VARCHAR(30) NOT NULL,
    source_account_id UUID,
    destination_account_id UUID,
    amount NUMERIC(19, 2) NOT NULL,
    description VARCHAR(255),
    failure_reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_bank_transactions_idempotency_key
        UNIQUE (idempotency_key),

    CONSTRAINT ck_bank_transactions_amount_positive
        CHECK (amount > 0),

    CONSTRAINT ck_bank_transactions_accounts
        CHECK (
            source_account_id IS NOT NULL
            OR destination_account_id IS NOT NULL
        )
);

CREATE INDEX idx_bank_transactions_source_account
    ON bank_transactions (source_account_id);

CREATE INDEX idx_bank_transactions_destination_account
    ON bank_transactions (destination_account_id);

CREATE INDEX idx_bank_transactions_status
    ON bank_transactions (transaction_status);

CREATE INDEX idx_bank_transactions_created_at
    ON bank_transactions (created_at);