package com.sidney.banking.customer.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository
        extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByUserId(UUID userId);

    Optional<Customer> findByCpf(String cpf);

    boolean existsByUserId(UUID userId);

    boolean existsByCpf(String cpf);
}
