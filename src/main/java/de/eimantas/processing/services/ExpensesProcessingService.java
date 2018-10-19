package de.eimantas.processing.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.eimantas.processing.clients.ExpensesClient;
import de.eimantas.processing.data.processing.ProcessingHelper;
import de.eimantas.processing.entities.EntityTransaction;
import de.eimantas.processing.entities.types.EntityTransactionType;
import de.eimantas.processing.messaging.TransactionSender;
import de.eimantas.processing.repo.TransactionRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.LinkedHashMap;

@Service
public class ExpensesProcessingService {

  private static final Logger logger = LoggerFactory.getLogger(ExpensesProcessingService.class);

  @Inject
  TransactionRepository repository;

  @Inject
  TransactionSender transactionSender;

  @Inject
  ExpensesClient expensesClient;

  public void processExpense(long expenseId) {
    logger.info("starting process expense with id: " + expenseId);
    JSONObject expenseJson = getExpenseInfo(expenseId);
    BigDecimal amount = getExpenseAmount(expenseJson);
    BigDecimal netto = ProcessingHelper.calculateNetto(amount);

    logger.info("got brutto : " + amount + " and calculated netto: " + netto);
    EntityTransaction transaction = ProcessingHelper.createInitTransaction(EntityTransactionType.EXPENSE, expenseId);

    transaction.setAccountId(getExpenseAccount(expenseJson));
    transaction.setAmountBefore(amount);
    transaction.setAmountAfter(netto);

    logger.info("saving transaction: " + transaction);
    EntityTransaction saved = repository.save(transaction);

    ObjectMapper mapper = new ObjectMapper();
    try {
      String body = mapper.writeValueAsString(saved);
      logger.info("Sending body: " + body);
      transactionSender.sendProcessedNotification(body);
    } catch (JsonProcessingException e) {
      logger.error("error in processing to json ",e);
      e.printStackTrace();
    }

  }

  public BigDecimal getExpenseAmount(JSONObject expenseJson) {

    try {
      String amount = expenseJson.getString("betrag");
      logger.info("amount in String is: " + amount);
      return new BigDecimal(amount);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return BigDecimal.ZERO;

  }

  public int getExpenseAccount(JSONObject expenseJson) {
    try {
      int accountId = expenseJson.getInt("accountId");
      logger.info("account ID is: " + accountId);
      return accountId;
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return 0;
  }


  public JSONObject getExpenseInfo(long expenseId) {
    ResponseEntity response = expensesClient.getExpenseById(expenseId);
    logger.info("response message is: " + response.getBody().toString());
    JSONObject json = new JSONObject((LinkedHashMap) response.getBody());
    return json;

  }


}
