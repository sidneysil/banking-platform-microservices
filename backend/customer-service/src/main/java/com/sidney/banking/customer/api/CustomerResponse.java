package com.sidney.banking.customer.api;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.sidney.banking.customer.domain.Customer;
import com.sidney.banking.customer.domain.CustomerStatus;

public record CustomerResponse(
        UUID id,
        UUID userId,
        String fullName,
        String cpf,
        LocalDate birthDate,
        String phone,
        CustomerStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getUserId(),
                customer.getFullName(),
                customer.getCpf(),
                customer.getBirthDate(),
                customer.getPhone(),
                customer.getStatus(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}