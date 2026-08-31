package com.sidney.banking.transaction.client;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestAccountClient implements AccountClient {

    private final RestClient restClient;

    public RestAccountClient(          
            @Value("${clients.account-service.base-url}")
            String accountServiceBaseUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(accountServiceBaseUrl)
                .build();
    }

   @Override
    public AccountTransferResponse executeTransfer(
            UUID sourceAccountId,
            UUID destinationAccountId,
            BigDecimal amount) {

        AccountTransferRequest request =
                new AccountTransferRequest(
                        sourceAccountId,
                        destinationAccountId,
                        amount
                );

        return restClient
                .post()
                .uri("/api/accounts/internal/transfers")
                .body(request)
                .retrieve()
                .body(AccountTransferResponse.class);
    }
}