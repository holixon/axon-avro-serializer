package io.holixon.axon.avro.examples.bankaccount

import bankaccount.command.CreateBankAccount
import bankaccount.command.DepositMoney
import bankaccount.command.WithdrawMoney
import bankaccount.projection.CurrentBalanceProjection
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bankaccounts")
class BankAccountController(
  private val commandGateway: CommandGateway,
  private val currentBalanceQueries: CurrentBalanceProjection.CurrentBalanceQueries
) {

  @PostMapping("/{bankAccountId}")
  fun create(
    @PathVariable("bankAccountId") bankAccountId: String,
    @RequestParam(value = "initialBalance", required = false) initialBalance: Int?
  ): ResponseEntity<Void> {
    commandGateway.sendAndWait<CreateBankAccount>(CreateBankAccount(
      bankAccountId, initialBalance ?: 0
    ))
    return ResponseEntity.ok().build()
  }

  @PutMapping("/{bankAccountId}")
  fun update(
    @PathVariable("bankAccountId") bankAccountId: String?,
    @RequestParam(value = "amount") amount: Int
  ): ResponseEntity<Void?>? {
    require(amount != 0) { "amount must be non zero" }
    if (amount > 0) {
      commandGateway.sendAndWait<DepositMoney>(
        DepositMoney(
          bankAccountId,
          amount
        )
      )
    } else {
      commandGateway.sendAndWait<WithdrawMoney>(WithdrawMoney(bankAccountId, -amount))
    }
    return ResponseEntity.ok().build()
  }

  @GetMapping("/{bankAccountId}")
  fun findById(
    @PathVariable("bankAccountId") bankAccountId: String
  ): ResponseEntity<CurrentBalanceProjection.CurrentBalance> {
    return ResponseEntity.of(
      currentBalanceQueries.findByAccountId(bankAccountId).join()
    )
  }

  @GetMapping("/")
  fun findAll(): ResponseEntity<List<CurrentBalanceProjection.CurrentBalance>> {
    return ResponseEntity.ok(
      currentBalanceQueries.findAll().join()
    )
  }
}
