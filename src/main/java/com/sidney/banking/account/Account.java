package com.sidney.banking.account;
import jakarta.persistence.*;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="accounts")
public class Account {
  @Id private UUID id; @Column(nullable=false) private UUID customerId;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private AccountType type;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private AccountStatus status;
  @Column(nullable=false,precision=19,scale=2) private BigDecimal balance;
  @Column(nullable=false,length=3) private String currency; @Version private long version;
  @Column(nullable=false) private Instant createdAt; @Column(nullable=false) private Instant updatedAt;
  protected Account() {}
  public Account(UUID customerId, AccountType type) { this.id=UUID.randomUUID(); this.customerId=customerId; this.type=type; this.status=AccountStatus.ACTIVE; this.balance=BigDecimal.ZERO.setScale(2); this.currency="BRL"; this.createdAt=this.updatedAt=Instant.now(); }
  public void credit(BigDecimal amount){ validate(amount); balance=balance.add(amount); updatedAt=Instant.now(); }
  public void debit(BigDecimal amount){ validate(amount); if(balance.compareTo(amount)<0) throw new IllegalStateException("Saldo insuficiente"); balance=balance.subtract(amount); updatedAt=Instant.now(); }
  private void validate(BigDecimal amount){ if(status!=AccountStatus.ACTIVE) throw new IllegalStateException("Conta inativa"); if(amount==null||amount.signum()<=0) throw new IllegalArgumentException("Valor deve ser positivo"); }
  public UUID getId(){return id;} public UUID getCustomerId(){return customerId;} public AccountType getType(){return type;} public AccountStatus getStatus(){return status;} public BigDecimal getBalance(){return balance;} public String getCurrency(){return currency;}
}
enum AccountStatus { ACTIVE, BLOCKED, CLOSED }
