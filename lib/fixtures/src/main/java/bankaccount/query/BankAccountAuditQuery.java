package bankaccount.query;

import bankaccount.projection.CurrentBalanceProjection.BankAccountAuditEvent;
import bankaccount.query.BankAccountAuditQuery.BankAccountAuditEvents;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;

public interface BankAccountAuditQuery extends Function<String, BankAccountAuditEvents> {

  static BankAccountAuditQuery create(final QueryGateway queryGateway) {
    return accountId -> queryGateway
      .query(new FindBankAccountAuditEventByAccountId(accountId), ResponseTypes.instanceOf(BankAccountAuditEvents.class));
  }

  @Override
  default BankAccountAuditEvents apply(String accountId) {
    return findByAccountId(accountId).join();
  }

  CompletableFuture<BankAccountAuditEvents> findByAccountId(String accountId);

  class FindBankAccountAuditEventByAccountId {

    private final String accountId;

    public FindBankAccountAuditEventByAccountId(String accountId) {
      this.accountId = accountId;
    }

    public String getAccountId() {
      return accountId;
    }
  }

  class BankAccountAuditEvents {

    private final List<BankAccountAuditEvent> events;

    public BankAccountAuditEvents(List<BankAccountAuditEvent> events) {
      this.events = events;
    }

    public List<BankAccountAuditEvent> getEvents() {
      return events;
    }

    @Override
    public String toString() {
      return "BankAccountAuditEvents{" +
        "events=" + events +
        '}';
    }
  }
}
