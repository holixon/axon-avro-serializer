package io.toolisticon.axon.avro.examples.bankaccount.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Objects;

public class CreateBankAccountCommand {

  @TargetAggregateIdentifier
  private final String bankAccountId;

  private final int initialBalance;

  public CreateBankAccountCommand(String bankAccountId, int initialBalance) {
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
    CreateBankAccountCommand that = (CreateBankAccountCommand) o;
    return initialBalance == that.initialBalance &&
      Objects.equals(bankAccountId, that.bankAccountId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bankAccountId, initialBalance);
  }

  @Override
  public String toString() {
    return "CreateBankAccountCommand{" +
      "bankAccountId='" + bankAccountId + '\'' +
      ", initialBalance=" + initialBalance +
      '}';
  }
}
