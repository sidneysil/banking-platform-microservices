CREATE TABLE customers (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    birth_date DATE NOT NULL,
    phone VARCHAR(20),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uk_customers_user_id UNIQUE (user_id),
    CONSTRAINT uk_customers_cpf UNIQUE (cpf),
    CONSTRAINT ck_customers_status
        CHECK (status IN ('ACTIVE', 'BLOCKED', 'INACTIVE'))
);

CREATE INDEX idx_customers_user_id ON customers(user_id);
CREATE INDEX idx_customers_cpf ON customers(cpf);
CREATE INDEX idx_customers_status ON customers(status);