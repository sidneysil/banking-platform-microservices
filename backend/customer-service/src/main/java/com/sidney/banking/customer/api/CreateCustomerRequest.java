package com.sidney.banking.customer.api;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(

        @NotBlank(message = "O nome completo é obrigatório")
        @Size(
                max = 120,
                message = "O nome deve possuir no máximo 120 caracteres"
        )
        String fullName,

        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(
                regexp = "\\d{11}",
                message = "O CPF deve conter exatamente 11 números"
        )
        String cpf,

        @NotNull(message = "A data de nascimento é obrigatória")
        @Past(message = "A data de nascimento deve estar no passado")
        LocalDate birthDate,

        @Size(
                max = 20,
                message = "O telefone deve possuir no máximo 20 caracteres"
        )
        String phone

) {
}