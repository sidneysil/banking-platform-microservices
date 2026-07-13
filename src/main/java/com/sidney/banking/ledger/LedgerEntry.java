package com.sidney.banking.ledger;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="ledger_entries")
public class LedgerEntry {
 @Id private UUID id; @Column(nullable=false) private UUID accountId; @Column(nullable=false,unique=true) private UUID operationId; @Column(nullable=false,unique=true) private String idempotencyKey;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private EntryType type; @Column(nullable=false,precision=19,scale=2) private BigDecimal amount; @Column(nullable=false,precision=19,scale=2) private BigDecimal balanceAfter; @Column(nullable=false) private String description; @Column(nullable=false) private Instant occurredAt;
 protected LedgerEntry(){} public LedgerEntry(UUID accountId,UUID operationId,String key,EntryType type,BigDecimal amount,BigDecimal after,String description){this.id=UUID.randomUUID();this.accountId=accountId;this.operationId=operationId;this.idempotencyKey=key;this.type=type;this.amount=amount;this.balanceAfter=after;this.description=description;this.occurredAt=Instant.now();}
 public UUID getId(){return id;} public UUID getAccountId(){return accountId;} public UUID getOperationId(){return operationId;} public EntryType getType(){return type;} public BigDecimal getAmount(){return amount;} public BigDecimal getBalanceAfter(){return balanceAfter;} public String getDescription(){return description;} public Instant getOccurredAt(){return occurredAt;}
}
