package com.sidney.banking.transaction.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidney.banking.transaction.dto.CreateTransactionRequest;
import com.sidney.banking.transaction.dto.TransactionResponse;
import com.sidney.banking.transaction.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @RequestBody CreateTransactionRequest request) {

        TransactionResponse response =
                transactionService.create(request);

        URI location = URI.create(
                "/api/transactions/" + response.id());

        return ResponseEntity
                .created(location)
                .body(response);
    }
}
