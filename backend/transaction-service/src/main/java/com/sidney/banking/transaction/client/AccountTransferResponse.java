package com.sidney.banking.transaction.client;

public record AccountTransferResponse(
        boolean success,
        String message
) {
}