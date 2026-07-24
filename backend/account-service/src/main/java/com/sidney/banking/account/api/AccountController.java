package com.sidney.banking.account.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidney.banking.account.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/me")
    public ResponseEntity<AccountResponse> create(

            @AuthenticationPrincipal Jwt jwt,

            @Valid
            @RequestBody CreateAccountRequest request

    ) {

        UUID customerId = UUID.fromString(jwt.getSubject());

        AccountResponse response =
                accountService.create(customerId, request);

        return ResponseEntity
                .created(URI.create("/api/accounts/me"))
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<AccountResponse>> list(

            @AuthenticationPrincipal Jwt jwt

    ) {

        UUID customerId = UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(
                accountService.findByCustomerId(customerId)
        );
    }

}
