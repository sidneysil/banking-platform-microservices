package com.sidney.banking.transaction.client;

import java.util.UUID;

public interface AccountClient {

    void validateTransaction(
            UUID sourceAccountId,
            UUID destinationAccountId,
            String transactionType,
            String amount);

}