package com.sidney.banking.customer.security;

import java.util.UUID;

public record AuthenticatedUser(

        UUID userId,

        String email,

        String role

) {
}