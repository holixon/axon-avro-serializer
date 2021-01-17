package bankaccount;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CreateBankAccountCommand {

  @TargetAggregateIdentifier
  private final String accountId;
  private final int initialBalance;

  public CreateBankAccountCommand(String accountId, int initialBalance) {
    this.accountId = accountId;
    this.initialBalance = initialBalance;
  }
}
