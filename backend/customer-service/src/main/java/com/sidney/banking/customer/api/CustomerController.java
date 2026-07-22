package com.sidney.banking.customer.api;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidney.banking.customer.security.AuthenticatedUser;
import com.sidney.banking.customer.security.AuthenticatedUserProvider;
import com.sidney.banking.customer.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public CustomerController(
            CustomerService customerService,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.customerService = customerService;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @PostMapping("/me")
    public ResponseEntity<CustomerResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateCustomerRequest request
    ) {

        AuthenticatedUser authenticatedUser =
                authenticatedUserProvider.from(jwt);

        CustomerResponse response = customerService.create(
                authenticatedUser.userId(),
                request
        );

        return ResponseEntity
                .created(URI.create("/api/customers/me"))
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerResponse> me(
            @AuthenticationPrincipal Jwt jwt
    ) {

        AuthenticatedUser authenticatedUser =
                authenticatedUserProvider.from(jwt);

        CustomerResponse response =
                customerService.findByUserId(
                        authenticatedUser.userId()
                );

        return ResponseEntity.ok(response);
    }
}