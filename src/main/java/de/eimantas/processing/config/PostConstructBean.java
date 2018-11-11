package de.eimantas.processing.config;

import de.eimantas.processing.data.processing.ProcessingHelper;
import de.eimantas.processing.entities.EntityTransaction;
import de.eimantas.processing.entities.types.EntityTransactionType;
import de.eimantas.processing.repo.TransactionRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.transaction.annotation.Isolation.READ_UNCOMMITTED;

@Component
public class PostConstructBean implements ApplicationRunner {

  private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
  public static final String TEST_USER_ID = "9a204126-12b9-4efe-9d9b-3808aba51ba3";

  @Autowired
  private TransactionRepository transactionRepository;


  @Autowired
  private Environment environment;

  private void preFillData() {

    logger.info("Filling data");
    // For booking
    EntityTransaction transaction = new EntityTransaction();
    transaction.setAmountBefore(BigDecimal.ZERO);
    transaction.setAmountAfter(BigDecimal.TEN);
    transaction.setAccountId(1);
    transaction.setRefEntityId(11);
    transaction.setType(EntityTransactionType.BOOKING);
    transaction.setProcessingDate(LocalDateTime.now());
    transaction.setUserId(TEST_USER_ID);
    EntityTransaction savedBooking = transactionRepository.save(transaction);
    logger.info("saving transaction Booking : " + savedBooking);

    // for normal expense
    EntityTransaction transactionExpense = ProcessingHelper.createInitTransaction(EntityTransactionType.EXPENSE, 2);
    transactionExpense.setAccountId(1);
    transactionExpense.setAmountBefore(BigDecimal.TEN);
    transactionExpense.setUserId(TEST_USER_ID);
    transactionExpense.setAmountAfter(BigDecimal.TEN);
    EntityTransaction saved = transactionRepository.save(transactionExpense);
    logger.info("saving transactionExpense : " + saved);
  }

  // we allow read stuff that is not commited, because by generation of subsequent entities, it comes to id collision
  @Override
  @Transactional(isolation = READ_UNCOMMITTED)
  public void run(ApplicationArguments args) throws Exception {

    logger.info("Starting expenses backend controller");
    logger.info("eureka server: " + environment.getProperty("spring.application.name"));
    logger.info("active profiles: " + Arrays.asList(environment.getActiveProfiles()).toString());
    logger.info("default profiles: " + Arrays.asList(environment.getDefaultProfiles()).toString());
    logger.info("sonstige info: " + environment.toString());
    logger.info("allowed Profiles: " + environment.getProperty("spring.profiles"));

    if (environment.getProperty("spring.profiles") != null) {
      if (environment.getProperty("spring.profiles").contains("populate")) {
        logger.info("Stuff will be populated!");
        preFillData();
      }
    } else {
      logger.info("Profile doesnt populate data");
    }
  }
}
