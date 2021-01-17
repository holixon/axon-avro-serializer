package bankaccount.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class DepositMoney {

  @TargetAggregateIdentifier
  private final String accountId;
  private final int amount;

  public DepositMoney(String accountId, int amount) {
    this.accountId = accountId;
    this.amount = amount;
  }

  public String getAccountId() {
    return accountId;
  }

  public int getAmount() {
    return amount;
  }
}
