package com.sidney.banking.account.dto;

public record ValidateTransactionResponse(

        boolean success,
        String message

) {
}
