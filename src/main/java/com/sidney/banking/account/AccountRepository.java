package com.sidney.banking.account;
import jakarta.persistence.LockModeType; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
import java.util.*;
public interface AccountRepository extends JpaRepository<Account,UUID> {
  @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select a from Account a where a.id=:id") Optional<Account> findByIdForUpdate(@Param("id") UUID id);
  List<Account> findByType(AccountType type);
}
