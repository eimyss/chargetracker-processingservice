package de.eimantas.processing.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.eimantas.processing.clients.ExpensesClient;
import de.eimantas.processing.data.processing.ProcessingHelper;
import de.eimantas.processing.entities.EntityTransaction;
import de.eimantas.processing.entities.EntityTransactionError;
import de.eimantas.processing.entities.types.EntityTransactionType;
import de.eimantas.processing.messaging.TransactionSender;
import de.eimantas.processing.repo.TransactionErrorRepository;
import de.eimantas.processing.repo.TransactionRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;

@Service
public class ExpensesProcessingService {

  private static final Logger logger = LoggerFactory.getLogger(ExpensesProcessingService.class);

  @Inject
  TransactionRepository repository;

  @Inject
  TransactionErrorRepository transactionErrorRepository;

  @Inject
  TransactionSender transactionSender;

  @Inject
  ExpensesClient expensesClient;

  public void processExpense(String expenseContent) {
    logger.info("starting process expense with content: " + expenseContent);

    JSONObject expenseMessage = null;
    try {
      expenseMessage = new JSONObject(expenseContent);
      logger.info("Parsed: " + expenseMessage.toString());
    } catch (JSONException e) {
      logger.error("failed to parse json ", e);
      notifyFailedExpenseProcessing(0, e.getMessage());
    }

    if (expenseMessage != null) {

      int expenseId = 0;
      JSONObject expenseJson = null;
      try {
        expenseJson = getExpenseInfo(expenseMessage);
        expenseId = getEntityId(expenseJson);
        BigDecimal amount = getExpenseAmount(expenseJson);
        BigDecimal netto = ProcessingHelper.calculateNetto(amount);
        String userId = getUserId(expenseJson);
        logger.info("got brutto : " + amount + " and calculated netto: " + netto);
        EntityTransaction transaction = ProcessingHelper.createInitTransaction(EntityTransactionType.EXPENSE, expenseId);

        transaction.setAccountId(getExpenseAccount(expenseJson));
        transaction.setAmountBefore(amount);
        transaction.setUserId(userId);
        transaction.setAmountAfter(netto);
        logger.info("saving transaction: " + transaction);
        EntityTransaction saved = repository.save(transaction);

        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(saved);
        logger.info("Sending body: " + body);
        transactionSender.sendProcessedNotification(body);
      } catch (Exception e) {
        logger.error("error in processing to json ", e);
        notifyFailedExpenseProcessing(expenseId, e.getMessage());
      }

    } else {
      logger.warn("Expenes processing failed (see journal)");
    }

  }

  private JSONObject getExpenseInfo(JSONObject expenseMessage) throws JSONException {
    return expenseMessage.getJSONObject("objectJson");
  }

  public BigDecimal getExpenseAmount(JSONObject expenseJson) throws JSONException {
    String amount = expenseJson.getString("betrag");
    logger.info("amount in String is: " + amount);
    return new BigDecimal(amount);
  }


  private int getEntityId(JSONObject expense) throws JSONException {
    int refAccountId = expense.getInt("id");
    logger.info("Expense ID is: " + refAccountId);
    return refAccountId;
  }


  public int getExpenseAccount(JSONObject expenseJson) throws JSONException {
    int accountId = expenseJson.getInt("accountId");
    logger.info("account ID is: " + accountId);
    return accountId;
  }

  private String getUserId(JSONObject expenseJson) throws JSONException {
    String userId = expenseJson.getString("userId");
    logger.info("Project User ID is: " + userId);
    return userId;
  }



  private void notifyFailedExpenseProcessing(long expenseId, String message) {
    logger.info("Creating error transaction");
    EntityTransactionError error = new EntityTransactionError();
    error.setDate(Instant.now());
    error.setException(message);
    error.setMessage("Failed to process expense with id " + expenseId);
    error.setType(EntityTransactionType.EXPENSE);
    transactionErrorRepository.save(error);
  }


}
