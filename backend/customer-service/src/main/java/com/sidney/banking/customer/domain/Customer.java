package com.sidney.banking.customer.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private UUID userId;

    @Column(
            name = "full_name",
            nullable = false,
            length = 120
    )
    private String fullName;

    @Column(
            name = "cpf",
            nullable = false,
            unique = true,
            length = 11
    )
    private String cpf;

    @Column(
            name = "birth_date",
            nullable = false
    )
    private LocalDate birthDate;

    @Column(
            name = "phone",
            length = 20
    )
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private CustomerStatus status;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private OffsetDateTime updatedAt;

    /**
     * Construtor obrigatório para o JPA.
     */
    protected Customer() {
    }

    /**
     * Construtor usado para cadastrar um cliente.
     */
    public Customer(
            UUID userId,
            String fullName,
            String cpf,
            LocalDate birthDate,
            String phone
    ) {
        this.userId = userId;
        this.fullName = fullName;
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.phone = phone;
        this.status = CustomerStatus.ACTIVE;
    }

    /**
     * Executado automaticamente antes da primeira gravação.
     */
    @PrePersist
    private void beforeInsert() {
        OffsetDateTime now = OffsetDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = CustomerStatus.ACTIVE;
        }
    }

    /**
     * Atualiza a data sempre que o cliente for alterado.
     */
    @PreUpdate
    private void beforeUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updatePersonalData(
            String fullName,
            LocalDate birthDate,
            String phone
    ) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
    }

    public void block() {
        this.status = CustomerStatus.BLOCKED;
    }

    public void activate() {
        this.status = CustomerStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = CustomerStatus.INACTIVE;
    }
}
