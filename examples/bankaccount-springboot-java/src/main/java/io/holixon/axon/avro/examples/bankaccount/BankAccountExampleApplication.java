package io.holixon.axon.avro.examples.bankaccount;

import io.holixon.axon.avro.common.AvroSchemaRegistry;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({AxonServerConfiguration.class})
public class BankAccountExampleApplication implements CommandLineRunner {

  @Autowired
  private AvroSchemaRegistry schemaRegistry;

  @Override
  public void run(String... args) throws Exception {

  }

  /**
   * Starts the application.
   * @param args line params.
   */
  public static void main(String[] args) {
    SpringApplication.run(BankAccountExampleApplication.class, args);
  }
}
