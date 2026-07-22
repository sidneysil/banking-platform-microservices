package com.sidney.banking.customer.security;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {

    public AuthenticatedUser from(Jwt jwt) {

        String userId = jwt.getClaimAsString("userId");

        if (userId == null || userId.isBlank()) {
            userId = jwt.getSubject();
        }

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(
                    "Token não possui o identificador do usuário."
            );
        }

        String email = jwt.getClaimAsString("email");

        String role = jwt.getClaimAsString("role");

        return new AuthenticatedUser(
                parseUserId(userId),
                email,
                role
        );
    }

    private UUID parseUserId(String userId) {
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "O identificador do usuário presente no token é inválido."
            );
        }
    }
}