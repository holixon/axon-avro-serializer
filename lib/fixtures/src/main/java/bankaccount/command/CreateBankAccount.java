package bankaccount.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Objects;

public class CreateBankAccount {

  @TargetAggregateIdentifier
  private final String accountId;
  private final int initialBalance;

  public CreateBankAccount(String accountId, int initialBalance) {
    this.accountId = accountId;
    this.initialBalance = initialBalance;
  }

  public String getAccountId() {
    return accountId;
  }

  public int getInitialBalance() {
    return initialBalance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreateBankAccount that = (CreateBankAccount) o;
    return initialBalance == that.initialBalance &&
      accountId.equals(that.accountId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, initialBalance);
  }

  @Override
  public String toString() {
    return "CreateBankAccountCommand{" +
      "accountId='" + accountId + '\'' +
      ", initialBalance=" + initialBalance +
      '}';
  }
}
