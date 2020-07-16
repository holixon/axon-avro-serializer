package io.toolisticon.axon.avro.examples.bankaccount.aggregate;

import io.toolisticon.axon.avro.examples.bankaccount.command.CreateBankAccountCommand;
import io.toolisticon.axon.avro.examples.bankaccount.command.DepositMoneyCommand;
import io.toolisticon.axon.avro.examples.bankaccount.command.WithdrawMoneyCommand;
import io.toolisticon.axon.avro.examples.bankaccount.event.BankAccountCreatedEvent;
import io.toolisticon.axon.avro.examples.bankaccount.event.MoneyDepositedEvent;
import io.toolisticon.axon.avro.examples.bankaccount.event.MoneyWithdrawnEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class BankAccountAggregate {

  @AggregateIdentifier
  private String bankAccountId;

  private int currentBalance;

  @CommandHandler
  public static BankAccountAggregate create(final CreateBankAccountCommand cmd) {
//    apply(BankAccountCreatedEvent.newBuilder()
//      .setBankAccountId(cmd.getBankAccountId())
//      .setInitialBalance(cmd.getInitialBalance())
//      .build());

    apply(new BankAccountCreatedEvent(
      cmd.getBankAccountId(),
      cmd.getInitialBalance())
    );

    return new BankAccountAggregate();
  }

  @EventSourcingHandler
  void on(BankAccountCreatedEvent evt) {
    this.bankAccountId = evt.getBankAccountId();
    this.currentBalance = evt.getInitialBalance();
  }


  @CommandHandler
  void handle(DepositMoneyCommand cmd) {
    checkArgument(cmd.getAmount() > 0, "amount must be > 0");

//    apply(MoneyDepositedEvent.newBuilder()
//      .setBankAccountId(cmd.getBankAccountId())
//      .setAmount(cmd.getAmount())
//      .build());
//
    apply(new MoneyDepositedEvent(
      cmd.getBankAccountId(),
      UUID.randomUUID().toString(),
      cmd.getAmount()
    ));
  }

  @EventSourcingHandler
  void on(MoneyDepositedEvent evt) {
    this.currentBalance += evt.getAmount();
  }

  @CommandHandler
  void handle(WithdrawMoneyCommand cmd) {
    checkArgument(cmd.getAmount() > 0, "amount must be > 0");
    checkArgument(cmd.getAmount() <= currentBalance, "not enough current: balance: %s", currentBalance);

//    apply(MoneyWithdrawnEvent.newBuilder()
//      .setBankAccountId(cmd.getBankAccountId())
//      .setAmount(cmd.getAmount())
//      .build());
    apply(new MoneyWithdrawnEvent(
      cmd.getBankAccountId(),
      UUID.randomUUID().toString(),
      cmd.getAmount()
    ));
  }

  @EventSourcingHandler
  void on(MoneyWithdrawnEvent evt) {
    this.currentBalance -= evt.getAmount();
  }
}
