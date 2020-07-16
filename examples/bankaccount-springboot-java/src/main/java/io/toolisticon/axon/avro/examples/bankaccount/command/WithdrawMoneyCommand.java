package io.toolisticon.axon.avro.examples.bankaccount.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Objects;

public class WithdrawMoneyCommand {

  @TargetAggregateIdentifier
  private final String bankAccountId;
  private final int amount;

  public WithdrawMoneyCommand(String bankAccountId, int amount) {
    this.bankAccountId = bankAccountId;
    this.amount = amount;
  }

  public String getBankAccountId() {
    return bankAccountId;
  }

  public int getAmount() {
    return amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WithdrawMoneyCommand that = (WithdrawMoneyCommand) o;
    return amount == that.amount &&
      Objects.equals(bankAccountId, that.bankAccountId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bankAccountId, amount);
  }

  @Override
  public String toString() {
    return "WithdrawMoneyCommand{" +
      "bankAccountId='" + bankAccountId + '\'' +
      ", amount=" + amount +
      '}';
  }
}
