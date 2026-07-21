package com.sidney.banking.customer.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidney.banking.customer.api.CreateCustomerRequest;
import com.sidney.banking.customer.api.CustomerResponse;
import com.sidney.banking.customer.domain.Customer;
import com.sidney.banking.customer.domain.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(
            CustomerRepository customerRepository
    ) {
        this.customerRepository = customerRepository;
    }

    /**
     * Cadastra os dados pessoais do usuário autenticado.
     */
    @Transactional
    public CustomerResponse create(
            UUID userId,
            CreateCustomerRequest request
    ) {
        if (customerRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException(
                    "Este usuário já possui um cadastro de cliente"
            );
        }

        String normalizedCpf = normalizeCpf(request.cpf());

        if (customerRepository.existsByCpf(normalizedCpf)) {
            throw new IllegalArgumentException(
                    "CPF já cadastrado"
            );
        }

        Customer customer = new Customer(
                userId,
                request.fullName().trim(),
                normalizedCpf,
                request.birthDate(),
                normalizePhone(request.phone())
        );

        Customer savedCustomer =
                customerRepository.save(customer);

        return CustomerResponse.from(savedCustomer);
    }

    /**
     * Consulta o cliente associado ao usuário autenticado.
     */
    @Transactional(readOnly = true)
    public CustomerResponse findByUserId(UUID userId) {
        Customer customer = customerRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cadastro de cliente não encontrado"
                        )
                );

        return CustomerResponse.from(customer);
    }

    private String normalizeCpf(String cpf) {
        return cpf.replaceAll("\\D", "");
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        return phone.replaceAll("\\D", "");
    }
}
