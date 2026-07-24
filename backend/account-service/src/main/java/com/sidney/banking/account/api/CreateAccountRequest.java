package com.sidney.banking.account.api;

import com.sidney.banking.account.domain.AccountType;

import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(

        @NotNull
        AccountType type

) {
}