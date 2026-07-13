package com.sidney.banking.jobs;
import com.sidney.banking.account.*; import com.sidney.banking.ledger.EntryType; import com.sidney.banking.reconciliation.ReconciliationService; import org.springframework.beans.factory.annotation.*; import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Component; import java.math.BigDecimal; import java.time.LocalDate;
@Component @ConditionalOnProperty(name="banking.jobs.enabled",havingValue="true",matchIfMissing=true)
public class FinancialJobs {
 private final AccountRepository accounts; private final AccountService service; private final ReconciliationService reconciliation; @Value("${banking.interest.daily-rate}") BigDecimal rate; @Value("${banking.fee.monthly-amount}") BigDecimal fee;
 public FinancialJobs(AccountRepository a,AccountService s,ReconciliationService r){accounts=a;service=s;reconciliation=r;}
 @Scheduled(cron="0 5 0 * * *",zone="America/Sao_Paulo") public void interest(){for(var a:accounts.findByType(AccountType.SAVINGS)){var value=a.getBalance().multiply(rate).setScale(2,java.math.RoundingMode.HALF_EVEN);if(value.signum()>0)service.post(a.getId(),EntryType.INTEREST,value,"Apropriação diária de juros","interest:"+a.getId()+":"+LocalDate.now());}}
 @Scheduled(cron="0 10 1 1 * *",zone="America/Sao_Paulo") public void fees(){for(var a:accounts.findByType(AccountType.CHECKING)){if(a.getBalance().compareTo(fee)>=0)service.post(a.getId(),EntryType.FEE,fee,"Tarifa mensal","fee:"+a.getId()+":"+LocalDate.now().withDayOfMonth(1));}}
 @Scheduled(cron="0 0 3 * * *",zone="America/Sao_Paulo") public void reconcile(){reconciliation.reconcileAll();}
}
