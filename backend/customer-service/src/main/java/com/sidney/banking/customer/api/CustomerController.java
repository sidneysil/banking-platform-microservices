package com.sidney.banking.customer.api;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidney.banking.customer.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/me")
    public ResponseEntity<CustomerResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateCustomerRequest request
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        CustomerResponse response =
                customerService.create(userId, request);

        return ResponseEntity
                .created(URI.create("/api/customers/me"))
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerResponse> me(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        CustomerResponse response =
                customerService.findByUserId(userId);

        return ResponseEntity.ok(response);
    }
}
