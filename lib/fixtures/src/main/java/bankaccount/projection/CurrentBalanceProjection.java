package bankaccount.projection;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import bankaccount.event.BankAccountCreated;
import bankaccount.event.MoneyDeposited;
import bankaccount.event.MoneyWithdrawn;
import bankaccount.query.BankAccountAuditQuery.BankAccountAuditEvents;
import bankaccount.query.BankAccountAuditQuery.FindBankAccountAuditEventByAccountId;
import bankaccount.query.CurrentBalanceQueries;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.SequenceNumber;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.messaging.annotation.MetaDataValue;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrentBalanceProjection {

  private static final Logger LOGGER = LoggerFactory.getLogger(CurrentBalanceProjection.class);

  private final Map<String, Integer> accounts;
  private final Map<String, List<BankAccountAuditEvent>> auditEvents = new ConcurrentHashMap<>();

  public CurrentBalanceProjection() {
    this(new ConcurrentHashMap<>());
  }

  public CurrentBalanceProjection(final Map<String, Integer> accounts) {
    this.accounts = accounts;
  }

  @EventHandler
  public void on(
    BankAccountCreated evt,
    @SequenceNumber long sequenceNumber,
    @Timestamp Instant timestamp,
    @MetaDataValue("traceId") String traceId,
    @MetaDataValue("correlationId") String correlationId
  ) {
    accounts.put(evt.getAccountId(), evt.getInitialBalance());

    addAuditEvent(sequenceNumber, timestamp, evt.getAccountId(), evt.getInitialBalance(), traceId, correlationId);
  }

  @EventHandler
  public void on(MoneyWithdrawn evt,
    @SequenceNumber long sequenceNumber,
    @Timestamp Instant timestamp,
    @MetaDataValue("traceId") String traceId,
    @MetaDataValue("correlationId") String correlationId
  ) {
    newBalance(evt.getAccountId(), -evt.getAmount());

    addAuditEvent(sequenceNumber, timestamp, evt.getAccountId(), evt.getAmount(), traceId, correlationId);
  }

  @EventHandler
  public void on(MoneyDeposited evt,
    @SequenceNumber long sequenceNumber,
    @Timestamp Instant timestamp,
    @MetaDataValue("traceId") String traceId,
    @MetaDataValue("correlationId") String correlationId
  ) {
    newBalance(evt.getAccountId(), evt.getAmount());

    addAuditEvent(sequenceNumber, timestamp, evt.getAccountId(), evt.getAmount(), traceId, correlationId);
  }

  @QueryHandler
  public Optional<CurrentBalance> findCurrentBalanceById(CurrentBalanceQueries.CurrentBalanceQuery query) {
    return Optional.ofNullable(accounts.get(query.getAccountId())).map(it -> new CurrentBalance(query.getAccountId(), it));
  }

  @QueryHandler
  public List<CurrentBalance> findAll(CurrentBalanceQueries.FindAll query) {
    return accounts.entrySet().stream().map(it -> new CurrentBalance(it.getKey(), it.getValue())).collect(toList());
  }

  @QueryHandler
  public BankAccountAuditEvents findAuditEventsByAccountId(FindBankAccountAuditEventByAccountId query) {
    return new BankAccountAuditEvents(auditEvents.getOrDefault(query.getAccountId(), Collections.emptyList()));
  }

  private void addAuditEvent(long sequenceNumber, Instant timestamp, String accountId, int amount, String traceId, String correlationId) {
    BankAccountAuditEvent evt = new BankAccountAuditEvent(
      sequenceNumber,
      timestamp,
      accountId,
      amount,
      traceId,
      correlationId
    );

    if (!auditEvents.containsKey(accountId)) {
      auditEvents.put(accountId, new ArrayList<BankAccountAuditEvent>());
    }

    auditEvents.get(accountId).add(evt);

    LOGGER.info("received: {}", evt);
  }


  private void newBalance(String accountId, int amount) {
    accounts.compute(accountId, (k, v) -> requireNonNull(v) + amount);
  }

  public static class CurrentBalance {

    private final String accountId;
    private final int balance;

    public CurrentBalance(String accountId, int balance) {
      this.accountId = accountId;
      this.balance = balance;
    }

    public String getAccountId() {
      return accountId;
    }

    public int getBalance() {
      return balance;
    }

    @Override
    public String toString() {
      return "CurrentBalance{" +
        "accountId='" + accountId + '\'' +
        ", balance=" + balance +
        '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      CurrentBalance that = (CurrentBalance) o;
      return balance == that.balance &&
        accountId.equals(that.accountId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(accountId, balance);
    }
  }

  public static class BankAccountAuditEvent {

    private final long sequenceNumber;
    private final Instant timestamp;
    private final String accountId;
    private final int amount;
    private final String traceId;
    private final String correlationId;

    public BankAccountAuditEvent(long sequenceNumber, Instant timestamp, String accountId,
      int amount, String traceId, String correlationId) {
      this.sequenceNumber = sequenceNumber;
      this.timestamp = timestamp;
      this.accountId = accountId;
      this.amount = amount;
      this.traceId = traceId;
      this.correlationId = correlationId;
    }

    public long getSequenceNumber() {
      return sequenceNumber;
    }

    public Instant getTimestamp() {
      return timestamp;
    }

    public String getAccountId() {
      return accountId;
    }

    public int getAmount() {
      return amount;
    }

    public String getTraceId() {
      return traceId;
    }

    public String getCorrelationId() {
      return correlationId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      BankAccountAuditEvent that = (BankAccountAuditEvent) o;
      return sequenceNumber == that.sequenceNumber && timestamp.equals(that.timestamp) && accountId
        .equals(that.accountId) && amount == that.amount && traceId.equals(that.traceId)
        && correlationId.equals(that.correlationId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(sequenceNumber, timestamp, accountId, amount, traceId, correlationId);
    }

    @Override
    public String toString() {
      return "BankAccountAuditEvent{" +
        "sequenceNumber=" + sequenceNumber +
        ", timestamp=" + timestamp +
        ", accountId='" + accountId + '\'' +
        ", amount='" + amount + '\'' +
        ", traceId='" + traceId + '\'' +
        ", correlationId='" + correlationId + '\'' +
        '}';
    }
  }


}
