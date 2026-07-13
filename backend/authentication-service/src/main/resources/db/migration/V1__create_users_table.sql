CREATE TABLE users (
    id UUID PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_role
        CHECK (role IN ('CUSTOMER', 'EMPLOYEE', 'ADMIN')),
    CONSTRAINT ck_users_status
        CHECK (status IN ('ACTIVE', 'BLOCKED', 'DISABLED'))
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);