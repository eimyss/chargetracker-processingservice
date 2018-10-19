package de.eimantas.processing.messaging;

import de.eimantas.processing.services.ExpensesProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;


public class ExpensesReceiver {

  private static final Logger logger = LoggerFactory.getLogger(ExpensesReceiver.class);

  @Inject
  private ExpensesProcessingService expensesProcessingService;

  public ExpensesReceiver() {


  }

  //  @RabbitListener(queues = "orderServiceQueue")
  public void receive(String message) {
    logger.info("Received message '{}'", message);
  }

  public void handleMessage(Object message) throws IOException {

    logger.info("Expense created notification message '{}'", message);
   expensesProcessingService.processExpense((Long) message);

  }


}
