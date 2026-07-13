package com.sidney.banking.ledger;
import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.math.BigDecimal; import java.util.*;
public interface LedgerRepository extends JpaRepository<LedgerEntry,UUID>{
 Optional<LedgerEntry> findByIdempotencyKey(String key); List<LedgerEntry> findByAccountIdOrderByOccurredAtDesc(UUID accountId);
 @Query("select coalesce(sum(case when e.type in (com.sidney.banking.ledger.EntryType.CREDIT,com.sidney.banking.ledger.EntryType.INTEREST,com.sidney.banking.ledger.EntryType.REDEMPTION) then e.amount else -e.amount end),0) from LedgerEntry e where e.accountId=:id") BigDecimal calculateBalance(@Param("id") UUID id);
}
