package com.sidney.banking.ledger.domain;

public enum OutboxStatus {

    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}