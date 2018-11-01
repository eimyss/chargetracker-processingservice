package de.eimantas.processing.services;


import de.eimantas.processing.clients.ProjectsClient;
import de.eimantas.processing.entities.EntityTransaction;
import de.eimantas.processing.entities.EntityTransactionError;
import de.eimantas.processing.entities.types.EntityTransactionType;
import de.eimantas.processing.messaging.BookingSender;
import de.eimantas.processing.repo.TransactionErrorRepository;
import de.eimantas.processing.repo.TransactionRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;

@Service
public class BookingService {

  @Inject
  TransactionErrorRepository transactionErrorRepository;

  @Inject
  TransactionRepository transactionRepository;

  @Inject
  ProjectsClient projectsClient;

  @Inject
  BookingSender bookingSender;

  private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

  // that fucking formatter will haunt me for a long time....
  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


  public void processBooking(String message) {
    logger.info("Booking received: " + message + " getting info for the booking");

    JSONObject bookingMessage = null;
    try {
      bookingMessage = new JSONObject(message);
      logger.info("Parsed: " + bookingMessage.toString());
    } catch (JSONException e) {
      logger.error("failed to parse json ", e);
    }

    if (bookingMessage != null) {
      try {
        JSONObject booking = getBookingInfo(bookingMessage);
        String userToken = getUserToken(bookingMessage);
        logger.info("User token: " + userToken);
        long minutes = calculateHours(booking);
        int entityId = getEntityId(booking);
        int projectId = getProjectId(booking);
        logger.info("getting rate for project: " + projectId);
        JSONObject projectInfo = getInfoFromProject(userToken, projectId);
        BigDecimal rate = getRate(projectInfo);
        logger.info("rate is: " + rate);
        int accountID = getAccountId(projectInfo);
        logger.info("ref accID is: " + accountID);
        BigDecimal betrag = calculateAmount(minutes, rate);
        logger.info("betrag is: " + betrag);
        String userId = getUserId(booking);
        logger.info("userId is: " + userId);

        logger.debug("creating json");
        JSONObject json = new JSONObject();
        json.put("booking_id", message);
        json.put("UserId", userId);
        json.put("refAccountId", accountID);
        json.put("Amount", betrag);
        sendNotification(json);

        logger.info("Creating transaction");
        EntityTransaction transaction = new EntityTransaction();
        transaction.setAmountBefore(BigDecimal.ZERO);
        transaction.setAmountAfter(betrag);
        transaction.setAccountId(accountID);
        transaction.setRefEntityId(entityId);
        transaction.setType(EntityTransactionType.BOOKING);
        transaction.setProcessingDate(LocalDateTime.now());
        transactionRepository.save(transaction);
        logger.info(" transaction saved");
      } catch (JSONException e) {
        logger.error("failed to process json object from booking message", e);
        notifyFailedBooking(message, e.getMessage());
      }

    } else {
      notifyFailedBooking(message, null);
    }
  }

  private int getEntityId(JSONObject booking) throws JSONException {
    int refAccountId = booking.getInt("serverBookingId");
    logger.info("Project ref acc ID is: " + refAccountId);
    return refAccountId;
  }

  private String getUserToken(JSONObject bookingMessage) throws JSONException {
    return bookingMessage.getString("userToken");

  }

  public void notifyFailedBooking(String message, String eMessage) {

    logger.warn("Creating error transaction log entry");
    EntityTransactionError error = new EntityTransactionError();
    error.setMessage(message);
    error.setException(eMessage);
    error.setDate(Instant.now());
    error.setType(EntityTransactionType.BOOKING);
    transactionErrorRepository.save(error);

  }

  private String getUserId(JSONObject booking) throws JSONException {
    String userId = booking.getString("userId");
    logger.info("Project User ID is: " + userId);
    return userId;
  }

  private int getAccountId(JSONObject projectInfo) throws JSONException {
    int refAccountId = projectInfo.getInt("refBankAccountId");
    logger.info("Project ref acc ID is: " + refAccountId);
    return refAccountId;
  }

  private BigDecimal getRate(JSONObject projectInfo) throws JSONException {
    BigDecimal rate = new BigDecimal(projectInfo.getString("rate"));
    logger.info("Project rate is: " + rate);
    return rate;
  }


  public void sendNotification(JSONObject json) throws JSONException {
    bookingSender.sendProcessedNotification(json);
  }

  public BigDecimal calculateAmount(long minutes, BigDecimal rate) {

    BigDecimal ratePerMinute = rate.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
    logger.info("minute rate is: " + ratePerMinute);

    return ratePerMinute.multiply(BigDecimal.valueOf(minutes)).setScale(2, RoundingMode.HALF_UP);
  }

  public JSONObject getInfoFromProject(String token, int projectId) {

    logger.info("receiving rate for project id: " + projectId);
    ResponseEntity response = projectsClient.getProject("Bearer " + token, projectId);
    logger.info("response message is: " + response.getBody().toString());

    JSONObject json = new JSONObject((LinkedHashMap) response.getBody());
    logger.info(json.toString());

    return json;

  }


  public long calculateHours(JSONObject booking) throws JSONException {


    String begin = booking.getString("startdate");
    String end = booking.getString("endDate");

    LocalDateTime beginTime = LocalDateTime.parse(begin, formatter);
    LocalDateTime endTime = LocalDateTime.parse(end, formatter);

    long minutesBetween = ChronoUnit.MINUTES.between(beginTime, endTime);

    logger.info("Difference between " + begin + " and " + end + " in minutes are " + minutesBetween);

    return minutesBetween;


  }

  private int getProjectId(JSONObject booking) {
    try {
      int projectID = booking.getInt("projectId");
      logger.info("Project id is: " + projectID);
      return projectID;
    } catch (JSONException e) {
      logger.error("failed to get project id ", e);
    }
    return 0;
  }

  public JSONObject getBookingInfo(JSONObject jsonMessage) throws JSONException {
    return jsonMessage.getJSONObject("objectJson");
  }

}
