package io.toolisticon.axon.avro.examples.bankaccount.projection;

import io.toolisticon.axon.avro.examples.bankaccount.event.BankAccountCreatedEvent;
import io.toolisticon.axon.avro.examples.bankaccount.event.MoneyDepositedEvent;
import io.toolisticon.axon.avro.examples.bankaccount.event.MoneyWithdrawnEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CurrentBalanceProjection {


  private final Map<String, BankAccountInfoDto> store = new ConcurrentHashMap<>();


  @QueryHandler
  public Optional<BankAccountInfoDto> query(FindById query) {
    return Optional.ofNullable(store.get(query.getBankAccountId()));
  }

  @QueryHandler
  public List<BankAccountInfoDto> query(FindAll query) {
    return new ArrayList<>(store.values());
  }

  @EventHandler
  void on(BankAccountCreatedEvent evt) {
    store.put(evt.getBankAccountId(), new BankAccountInfoDto(evt.getBankAccountId(), evt.getInitialBalance()));
  }


  @EventHandler
  void on(MoneyDepositedEvent evt) {
    store.computeIfPresent(evt.getBankAccountId(), (key, current) -> new BankAccountInfoDto(
        evt.getBankAccountId(),
        current.currentBalance + evt.getAmount()
      )
    );
  }

  @EventHandler
  void on(MoneyWithdrawnEvent evt) {
    store.computeIfPresent(evt.getBankAccountId(), (key, current) -> new BankAccountInfoDto(
        evt.getBankAccountId(),
        current.currentBalance - evt.getAmount()
      )
    );
  }


  public static class FindById {
    private final String bankAccountId;

    public FindById(String bankAccountId) {
      this.bankAccountId = bankAccountId;
    }

    public String getBankAccountId() {
      return bankAccountId;
    }

    @Override
    public String toString() {
      return "FindById{" +
        "bankAccountId='" + bankAccountId + '\'' +
        '}';
    }
  }

  public static class FindAll {
  }


  public static class BankAccountInfoDto {

    private final String bankAccountId;
    private final int currentBalance;

    public BankAccountInfoDto(String bankAccountId, int currentBalance) {
      this.bankAccountId = bankAccountId;
      this.currentBalance = currentBalance;
    }

    public String getBankAccountId() {
      return bankAccountId;
    }

    public int getCurrentBalance() {
      return currentBalance;
    }

    @Override
    public String toString() {
      return "BankAccountInfoDto{" +
        "bankAccountId='" + bankAccountId + '\'' +
        ", currentBalance=" + currentBalance +
        '}';
    }
  }

}
