package bankaccount;

import bankaccount.command.CreateBankAccount;
import bankaccount.command.DepositMoney;
import bankaccount.command.WithdrawMoney;
import bankaccount.event.BankAccountCreated;
import bankaccount.event.MoneyDeposited;
import bankaccount.event.MoneyWithdrawn;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class BankAccount {

  @AggregateIdentifier
  protected String accountId;

  protected int balance;
  
  @CommandHandler
  public static BankAccount handle(CreateBankAccount cmd) {
    AggregateLifecycle.apply(BankAccountCreated.newBuilder()
      .setAccountId(cmd.getAccountId())
      .setInitialBalance(cmd.getInitialBalance())
      .build());
    return new BankAccount();
  }

  @CommandHandler
  public void handle(WithdrawMoney cmd) {
    if (cmd.getAmount() > balance) {
      throw new IllegalStateException(String.format("Unsufficient Balance: %d, cmd=%s", balance, cmd));
    }
    if (cmd.getAmount() <= 0) {
      throw new IllegalArgumentException("Amount has to be > 0, cmd=" + cmd);
    }
    AggregateLifecycle.apply(MoneyWithdrawn.newBuilder()
      .setAccountId(cmd.getAccountId())
      .setAmount(cmd.getAmount())
      .build());
  }

  @CommandHandler
  public void handle(DepositMoney cmd) {
    if (cmd.getAmount() <= 0) {
      throw new IllegalArgumentException("Amount has to be > 0, cmd=" + cmd);
    }
    AggregateLifecycle.apply(MoneyDeposited.newBuilder()
      .setAccountId(cmd.getAccountId())
      .setAmount(cmd.getAmount())
      .build());
  }

  @EventSourcingHandler
  public void on(BankAccountCreated evt) {
    this.accountId = evt.getAccountId();
    this.balance = evt.getInitialBalance();
  }

  @EventSourcingHandler
  public void on(MoneyDeposited evt) {
    this.accountId = evt.getAccountId();
    this.balance += evt.getAmount();
  }

  @EventSourcingHandler
  public void on(MoneyWithdrawn evt) {
    this.accountId = evt.getAccountId();
    this.balance -= evt.getAmount();
  }
}
