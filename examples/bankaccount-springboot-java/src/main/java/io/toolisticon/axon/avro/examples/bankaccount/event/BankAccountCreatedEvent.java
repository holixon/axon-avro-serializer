package io.toolisticon.axon.avro.examples.bankaccount.event;

import java.util.Objects;

public class BankAccountCreatedEvent {

  private final String bankAccountId;
  private final int initialBalance;

  public BankAccountCreatedEvent(String bankAccountId, int initialBalance) {
    this.bankAccountId = bankAccountId;
    this.initialBalance = initialBalance;
  }

  public String getBankAccountId() {
    return bankAccountId;
  }

  public int getInitialBalance() {
    return initialBalance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BankAccountCreatedEvent that = (BankAccountCreatedEvent) o;
    return initialBalance == that.initialBalance &&
      Objects.equals(bankAccountId, that.bankAccountId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bankAccountId, initialBalance);
  }

  @Override
  public String toString() {
    return "BankAccountCreatedEvent{" +
      "bankAccountId='" + bankAccountId + '\'' +
      ", initialBalance=" + initialBalance +
      '}';
  }
}
