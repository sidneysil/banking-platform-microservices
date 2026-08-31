package com.sidney.banking.transaction.client;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

        String token = getCurrentJwt();

        return restClient
                .post()
                .uri("/api/accounts/internal/transfers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(request)
                .retrieve()
                .body(AccountTransferResponse.class);
    }

    private String getCurrentJwt() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            throw new IllegalStateException(
                    "JWT não encontrado no contexto de segurança."
            );
        }

        return jwtAuthentication
                .getToken()
                .getTokenValue();
    }
}