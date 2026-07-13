package com.sidney.banking.account;
import com.sidney.banking.ledger.*; import jakarta.validation.Valid; import jakarta.validation.constraints.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.math.BigDecimal; import java.net.URI; import java.util.*;
@RestController @RequestMapping("/api/accounts")
public class AccountController {
 private final AccountService service; public AccountController(AccountService s){service=s;}
 @PostMapping public ResponseEntity<AccountView> create(@Valid @RequestBody CreateAccount r){var a=service.create(r.customerId(),r.type());return ResponseEntity.created(URI.create("/api/accounts/"+a.getId())).body(AccountView.of(a));}
 @GetMapping("/{id}") public AccountView get(@PathVariable UUID id){return AccountView.of(service.get(id));}
 @GetMapping("/{id}/statement") public List<LedgerEntry> statement(@PathVariable UUID id){return service.statement(id);}
 @PostMapping("/{id}/transactions") public ResponseEntity<LedgerEntry> post(@PathVariable UUID id,@RequestHeader("Idempotency-Key") @Size(max=100) String key,@Valid @RequestBody TransactionRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.post(id,r.type(),r.amount(),r.description(),key));}
 record CreateAccount(@NotNull UUID customerId,@NotNull AccountType type){} record TransactionRequest(@NotNull EntryType type,@NotNull @DecimalMin("0.01") @Digits(integer=17,fraction=2) BigDecimal amount,@NotBlank @Size(max=255) String description){}
 record AccountView(UUID id,UUID customerId,AccountType type,String status,BigDecimal balance,String currency){static AccountView of(Account a){return new AccountView(a.getId(),a.getCustomerId(),a.getType(),a.getStatus().name(),a.getBalance(),a.getCurrency());}}
}
