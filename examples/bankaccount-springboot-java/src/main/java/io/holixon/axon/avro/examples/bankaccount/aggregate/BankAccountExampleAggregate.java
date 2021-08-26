package io.holixon.axon.avro.examples.bankaccount.aggregate;

import example.bankaccount.event.BankAccountCreatedEvent;
import example.bankaccount.event.MoneyDepositedEvent;
import example.bankaccount.event.MoneyWithdrawnEvent;
import io.holixon.axon.avro.examples.bankaccount.command.CreateBankAccountCommand;
import io.holixon.axon.avro.examples.bankaccount.command.DepositMoneyCommand;
import io.holixon.axon.avro.examples.bankaccount.command.WithdrawMoneyCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static com.google.common.base.Preconditions.checkArgument;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class BankAccountExampleAggregate  {

  @AggregateIdentifier
  private String bankAccountId;
  private int currentBalance;

  @CommandHandler
  public static BankAccountExampleAggregate create(final CreateBankAccountCommand cmd) {
    apply(BankAccountCreatedEvent.newBuilder()
      .setBankAccountId(cmd.getBankAccountId())
      .setInitialBalance(cmd.getInitialBalance())
      .build());
    return new BankAccountExampleAggregate();
  }

  @CommandHandler
  void handle(DepositMoneyCommand cmd) {
    checkArgument(cmd.getAmount() > 0, "amount must be > 0");

    apply(MoneyDepositedEvent.newBuilder()
      .setBankAccountId(cmd.getBankAccountId())
      .setAmount(cmd.getAmount())
      .build());
  }

  @CommandHandler
  void handle(WithdrawMoneyCommand cmd) {
    checkArgument(cmd.getAmount() > 0, "amount must be > 0");
    checkArgument(cmd.getAmount() <= currentBalance, "not enough current: balance: %s", currentBalance);

    apply(MoneyWithdrawnEvent.newBuilder()
      .setBankAccountId(cmd.getBankAccountId())
      .setAmount(cmd.getAmount())
      .build());
  }

  @EventSourcingHandler
  void on(MoneyDepositedEvent evt) {
    this.currentBalance += evt.getAmount();
  }

  @EventSourcingHandler
  void on(BankAccountCreatedEvent evt) {
    this.bankAccountId = evt.getBankAccountId();
    this.currentBalance = evt.getInitialBalance();
  }

  @EventSourcingHandler
  void on(MoneyWithdrawnEvent evt) {
    this.currentBalance -= evt.getAmount();
  }
}
