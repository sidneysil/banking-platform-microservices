package com.sidney.banking.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank
        @Size(min = 3, max = 120)
        String fullName,

        @NotBlank
        @Email
        @Size(max = 180)
        String email,

        @NotBlank
        @Size(min = 8, max = 72)
        String password
) {
}