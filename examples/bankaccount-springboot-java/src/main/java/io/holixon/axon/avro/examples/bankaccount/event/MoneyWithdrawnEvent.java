package io.holixon.axon.avro.examples.bankaccount.event;

import java.util.Objects;

public class MoneyWithdrawnEvent {

  private final String bankAccountId;
  private final String transactionId;
  private final int amount;

  public MoneyWithdrawnEvent(String bankAccountId, String transactionId, int amount) {
    this.bankAccountId = bankAccountId;
    this.transactionId = transactionId;
    this.amount = amount;
  }

  public String getBankAccountId() {
    return bankAccountId;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public int getAmount() {
    return amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MoneyWithdrawnEvent that = (MoneyWithdrawnEvent) o;
    return amount == that.amount &&
      Objects.equals(bankAccountId, that.bankAccountId) &&
      Objects.equals(transactionId, that.transactionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bankAccountId, transactionId, amount);
  }

  @Override
  public String toString() {
    return "MoneyWithdrawnEvent{" +
      "bankAccountId='" + bankAccountId + '\'' +
      ", transactionId='" + transactionId + '\'' +
      ", amount=" + amount +
      '}';
  }
}
