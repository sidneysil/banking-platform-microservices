package com.sidney.banking.transaction.client;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountClient {

    AccountTransferResponse executeTransfer(
            UUID sourceAccountId,
            UUID destinationAccountId,
            BigDecimal amount);
}