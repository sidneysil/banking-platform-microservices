CREATE TABLE accounts (
    id UUID PRIMARY KEY,

    customer_id UUID NOT NULL,

    agency VARCHAR(10) NOT NULL,

    account_number VARCHAR(20) NOT NULL,

    type VARCHAR(20) NOT NULL,

    status VARCHAR(20) NOT NULL,

    balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uk_accounts_account_number
        UNIQUE (account_number),

    CONSTRAINT ck_accounts_type
        CHECK (type IN ('CURRENT', 'SAVINGS')),

    CONSTRAINT ck_accounts_status
        CHECK (status IN ('ACTIVE', 'BLOCKED', 'CLOSED')),

    CONSTRAINT ck_accounts_balance
        CHECK (balance >= 0)
);

CREATE INDEX idx_accounts_customer_id
    ON accounts (customer_id);

CREATE INDEX idx_accounts_status
    ON accounts (status);