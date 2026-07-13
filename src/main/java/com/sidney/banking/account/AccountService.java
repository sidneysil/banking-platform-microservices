package com.sidney.banking.account;
import com.fasterxml.jackson.core.JsonProcessingException; import com.fasterxml.jackson.databind.ObjectMapper; import com.sidney.banking.ledger.*; import com.sidney.banking.outbox.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal; import java.util.*;
@Service
public class AccountService {
 private final AccountRepository accounts; private final LedgerRepository ledger; private final OutboxRepository outbox; private final ObjectMapper json;
 public AccountService(AccountRepository a,LedgerRepository l,OutboxRepository o,ObjectMapper j){accounts=a;ledger=l;outbox=o;json=j;}
 @Transactional public Account create(UUID customerId,AccountType type){return accounts.save(new Account(customerId,type));}
 @Transactional(readOnly=true) public Account get(UUID id){return accounts.findById(id).orElseThrow(()->new NoSuchElementException("Conta não encontrada"));}
 @Transactional(readOnly=true) public List<LedgerEntry> statement(UUID id){get(id);return ledger.findByAccountIdOrderByOccurredAtDesc(id);}
 @Transactional public LedgerEntry post(UUID accountId,EntryType type,BigDecimal amount,String description,String key){
   var old=ledger.findByIdempotencyKey(key); if(old.isPresent()) return old.get();
   var account=accounts.findByIdForUpdate(accountId).orElseThrow(()->new NoSuchElementException("Conta não encontrada"));
   if(type==EntryType.CREDIT||type==EntryType.INTEREST||type==EntryType.REDEMPTION) account.credit(amount); else account.debit(amount);
   var operationId=UUID.randomUUID(); var entry=ledger.save(new LedgerEntry(accountId,operationId,key,type,amount,account.getBalance(),description));
   var eventId=UUID.randomUUID(); var payload=toJson(new AccountEvent(eventId,operationId,accountId,type.name(),amount,account.getBalance(),description));
   outbox.save(new OutboxEvent(eventId,accountId,"AccountBalanceChanged",payload)); return entry;
 }
 private String toJson(Object value){try{return json.writeValueAsString(value);}catch(JsonProcessingException e){throw new IllegalStateException(e);}}
 public record AccountEvent(UUID eventId,UUID operationId,UUID accountId,String type,BigDecimal amount,BigDecimal balanceAfter,String description){}
}
