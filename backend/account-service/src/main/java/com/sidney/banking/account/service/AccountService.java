package com.sidney.banking.account.service;

import com.sidney.banking.account.domain.Account;
import com.sidney.banking.account.domain.AccountStatus;
import com.sidney.banking.account.dto.ValidateTransactionRequest;
import com.sidney.banking.account.dto.ValidateTransactionResponse;
import com.sidney.banking.account.outbox.OutboxService;
import com.sidney.banking.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final OutboxService outboxService;

    public AccountService(
            AccountRepository accountRepository,
            OutboxService outboxService
    ) {
        this.accountRepository = accountRepository;
        this.outboxService = outboxService;
    }

    @Transactional
    public ValidateTransactionResponse executeTransfer(
            ValidateTransactionRequest request
    ) {
        validateRequest(request);

        if (request.sourceAccountId().equals(request.destinationAccountId())) {
            throw new IllegalArgumentException(
                    "A conta de origem não pode ser igual à conta de destino."
            );
        }

        UUID firstAccountId = getFirstAccountId(
                request.sourceAccountId(),
                request.destinationAccountId()
        );

        UUID secondAccountId = getSecondAccountId(
                request.sourceAccountId(),
                request.destinationAccountId()
        );

        Account firstAccount = findAccountForUpdate(firstAccountId);
        Account secondAccount = findAccountForUpdate(secondAccountId);

        Account sourceAccount = resolveAccount(
                request.sourceAccountId(),
                firstAccount,
                secondAccount
        );

        Account destinationAccount = resolveAccount(
                request.destinationAccountId(),
                firstAccount,
                secondAccount
        );

        validateAccountIsActive(sourceAccount, "origem");
        validateAccountIsActive(destinationAccount, "destino");
        validateAvailableBalance(sourceAccount, request.amount());

        sourceAccount.debit(request.amount());
        destinationAccount.credit(request.amount());

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
        
        outboxService.publishTransfer(
                sourceAccount.getId(),
                destinationAccount.getId(),
                request.amount()
        );

        return new ValidateTransactionResponse(
                true,
                "Transferência processada com sucesso."
        );
    }

    private void validateRequest(ValidateTransactionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Os dados da transferência são obrigatórios."
            );
        }

        if (request.sourceAccountId() == null) {
            throw new IllegalArgumentException(
                    "A conta de origem é obrigatória."
            );
        }

        if (request.destinationAccountId() == null) {
            throw new IllegalArgumentException(
                    "A conta de destino é obrigatória."
            );
        }

        if (request.amount() == null
                || request.amount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "O valor da transferência deve ser maior que zero."
            );
        }
    }

    private Account findAccountForUpdate(UUID accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Conta não encontrada: " + accountId
                ));
    }

    private void validateAccountIsActive(
            Account account,
            String accountRole
    ) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "A conta de " + accountRole + " não está ativa."
            );
        }
    }

    private void validateAvailableBalance(
            Account sourceAccount,
            BigDecimal amount
    ) {
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente para realizar a transferência."
            );
        }
    }

    private Account resolveAccount(
            UUID expectedAccountId,
            Account firstAccount,
            Account secondAccount
    ) {
        if (firstAccount.getId().equals(expectedAccountId)) {
            return firstAccount;
        }

        return secondAccount;
    }

    private UUID getFirstAccountId(
            UUID sourceAccountId,
            UUID destinationAccountId
    ) {
        return sourceAccountId.compareTo(destinationAccountId) < 0
                ? sourceAccountId
                : destinationAccountId;
    }

    private UUID getSecondAccountId(
            UUID sourceAccountId,
            UUID destinationAccountId
    ) {
        return sourceAccountId.compareTo(destinationAccountId) < 0
                ? destinationAccountId
                : sourceAccountId;
    }
}