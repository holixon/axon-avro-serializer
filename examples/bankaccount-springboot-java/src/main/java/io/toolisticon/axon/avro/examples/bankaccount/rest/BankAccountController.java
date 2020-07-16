package io.toolisticon.axon.avro.examples.bankaccount.rest;

import io.toolisticon.axon.avro.examples.bankaccount.command.CreateBankAccountCommand;
import io.toolisticon.axon.avro.examples.bankaccount.command.DepositMoneyCommand;
import io.toolisticon.axon.avro.examples.bankaccount.command.WithdrawMoneyCommand;
import io.toolisticon.axon.avro.examples.bankaccount.projection.CurrentBalanceProjection;
import io.toolisticon.axon.avro.examples.bankaccount.projection.CurrentBalanceProjection.BankAccountInfoDto;
import io.toolisticon.axon.avro.examples.bankaccount.projection.CurrentBalanceProjection.FindAll;
import io.toolisticon.axon.avro.examples.bankaccount.projection.CurrentBalanceProjection.FindById;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.axonframework.messaging.responsetypes.ResponseTypes.multipleInstancesOf;
import static org.axonframework.messaging.responsetypes.ResponseTypes.optionalInstanceOf;

@RestController
@RequestMapping("/api/bankaccounts")
public class BankAccountController {

  private final CommandGateway commandGateway;
  private final QueryGateway queryGateway;

  public BankAccountController(CommandGateway commandGateway, QueryGateway queryGateway) {
    this.commandGateway = commandGateway;
    this.queryGateway = queryGateway;
  }

  @PostMapping("/{bankAccountId}")
  public ResponseEntity<Void> create(
    @PathVariable("bankAccountId") String bankAccountId,
    @RequestParam(value = "initialBalance", required = false) Integer initialBalance
  ) {
    commandGateway.sendAndWait(
      new CreateBankAccountCommand(
        bankAccountId,
        Optional.ofNullable(initialBalance).orElse(0))
    );

    return ResponseEntity.ok().build();
  }

  @PutMapping("/{bankAccountId}")
  public ResponseEntity<Void> update(
    @PathVariable("bankAccountId") String bankAccountId,
    @RequestParam(value = "amount") int amount
  ) {
    if (amount == 0) {
      throw new IllegalArgumentException("amount must be non zero");
    } else if (amount > 0) {
      commandGateway.sendAndWait(
        new DepositMoneyCommand(
          bankAccountId,
          amount
        )
      );
    } else {
      commandGateway.sendAndWait(
        new WithdrawMoneyCommand(
          bankAccountId,
          -amount
        )
      );
    }

    return ResponseEntity.ok().build();
  }

  @GetMapping("/{bankAccountId}")
  public ResponseEntity<BankAccountInfoDto> findById(
    @PathVariable("bankAccountId") String bankAccountId
  ) {
    return ResponseEntity.of(
      queryGateway.query(
        new FindById(bankAccountId),
        optionalInstanceOf(BankAccountInfoDto.class)
      ).join()
    );
  }

  @GetMapping("/")
  public ResponseEntity<List<BankAccountInfoDto>> findAll() {
    return ResponseEntity.ok(
      queryGateway.query(
        new FindAll(),
        multipleInstancesOf(BankAccountInfoDto.class)
      ).join()
    );
  }

}
