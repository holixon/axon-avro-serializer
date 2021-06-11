package bankaccount.query;

import bankaccount.projection.CurrentBalanceProjection.CurrentBalance;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;

public class CurrentBalanceQueries {
  private final QueryGateway queryGateway;

  public CurrentBalanceQueries(QueryGateway queryGateway) {
    this.queryGateway = queryGateway;
  }

  public CompletableFuture<Optional<CurrentBalance>> findByAccountId(String accountId) {
    return queryGateway.query(new CurrentBalanceQuery(accountId), ResponseTypes.optionalInstanceOf(CurrentBalance.class));
  }

  public CompletableFuture<List<CurrentBalance>> findAll() {
    return queryGateway.query(FindAll.INSTANCE, ResponseTypes.multipleInstancesOf(CurrentBalance.class));
  }

  public static class CurrentBalanceQuery {

    private final String accountId;

    public CurrentBalanceQuery(String accountId) {
      this.accountId = accountId;
    }

    public String getAccountId() {
      return accountId;
    }
  }



  public static class FindAll {

    public static final FindAll INSTANCE = new FindAll();
  }
}
