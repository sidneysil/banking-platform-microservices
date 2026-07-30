package com.sidney.banking.account.api;

import com.sidney.banking.account.dto.ValidateTransactionRequest;
import com.sidney.banking.account.dto.ValidateTransactionResponse;
import com.sidney.banking.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/internal/transfers")
    public ResponseEntity<ValidateTransactionResponse> executeTransfer(
            @Valid @RequestBody ValidateTransactionRequest request
    ) {
        ValidateTransactionResponse response =
                accountService.executeTransfer(request);

        return ResponseEntity.ok(response);
    }
}