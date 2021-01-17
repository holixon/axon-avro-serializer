package bankaccount;

import bankaccount.command.CreateBankAccount;
import bankaccount.event.BankAccountCreated;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;

public class BankAccount {

  @AggregateIdentifier
  private String accountId;

  private int balance;

  @CommandHandler
  public static String handle(CreateBankAccount cmd) {
    AggregateLifecycle.apply(BankAccountCreated.newBuilder()
      .setAccountId(cmd.getAccountId())
      .setInitialBalance(cmd.getInitialBalance())
      .build());
    return cmd.getAccountId();
  }

  @EventSourcingHandler
  public void on(BankAccountCreated evt) {
    this.accountId = evt.getAccountId();
    this.balance = evt.getInitialBalance();
  }

}
