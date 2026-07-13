package com.sidney.banking.account;
import org.junit.jupiter.api.Test; import java.math.BigDecimal; import java.util.UUID; import static org.junit.jupiter.api.Assertions.*;
class AccountTest { @Test void creditAndDebit(){var a=new Account(UUID.randomUUID(),AccountType.CHECKING);a.credit(new BigDecimal("100.00"));a.debit(new BigDecimal("30.00"));assertEquals(new BigDecimal("70.00"),a.getBalance());} @Test void preventsOverdraft(){var a=new Account(UUID.randomUUID(),AccountType.CHECKING);assertThrows(IllegalStateException.class,()->a.debit(BigDecimal.ONE));} }
