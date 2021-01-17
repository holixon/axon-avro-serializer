package bankaccount.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class WithdrawMoney {

  @TargetAggregateIdentifier
  private final String accountId;
  private final int amount;

  public WithdrawMoney(String accountId, int amount) {
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
