package com.sidney.banking.account.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidney.banking.account.domain.Account;
import com.sidney.banking.account.domain.AccountStatus;
import com.sidney.banking.account.dto.ValidateTransactionRequest;
import com.sidney.banking.account.dto.ValidateTransactionResponse;
import com.sidney.banking.account.outbox.OutboxService;
import com.sidney.banking.account.repository.AccountRepository;

@Service
public class AccountService {

    private static final String ACCOUNTS_CACHE = "accounts";

    private final AccountRepository accountRepository;
    private final OutboxService outboxService;

    public AccountService(
            AccountRepository accountRepository,
            OutboxService outboxService
    ) {
        this.accountRepository = accountRepository;
        this.outboxService = outboxService;
    }

    /**
     * Consulta uma conta pelo identificador.
     *
     * Na primeira chamada, a conta é consultada no PostgreSQL e armazenada
     * no Redis. Nas próximas chamadas, enquanto o cache estiver válido,
     * o resultado será recuperado diretamente do Redis.
     */
    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = ACCOUNTS_CACHE,
            key = "#p0",
            condition = "#p0 != null"
    )
    public Account findById(UUID accountId) {

        if (accountId == null) {
            throw new IllegalArgumentException(
                    "O identificador da conta é obrigatório."
            );
        }

        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Conta não encontrada: " + accountId
                ));
    }

    /**
     * Executa uma transferência entre duas contas.
     *
     * Após uma transferência concluída com sucesso, as contas de origem
     * e destino são removidas do Redis para evitar a leitura de saldos
     * desatualizados.
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(
                    cacheNames = ACCOUNTS_CACHE,
                    key = "#p0.sourceAccountId()"
            ),
            @CacheEvict(
                    cacheNames = ACCOUNTS_CACHE,
                    key = "#p0.destinationAccountId()"
            )
    })
    public ValidateTransactionResponse executeTransfer(
            ValidateTransactionRequest request
    ) {
        validateRequest(request);

        if (request.sourceAccountId()
                .equals(request.destinationAccountId())) {

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

        /*
         * As contas são bloqueadas sempre na mesma ordem.
         *
         * Isso reduz o risco de deadlock quando duas transferências
         * concorrentes envolvem as mesmas contas.
         */
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

        /*
         * O evento é persistido pela estratégia Outbox dentro da operação
         * de negócio, garantindo publicação posterior no Kafka.
         */
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