package com.sidney.banking.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.sidney.banking.auth.user.User;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final long expirationSeconds;

    public TokenService(
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.expiration-seconds}") long expirationSeconds
    ) {
        this.jwtEncoder = jwtEncoder;
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(
                expirationSeconds,
                ChronoUnit.SECONDS
        );

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("banking-platform")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}