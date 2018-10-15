package de.eimantas.processing.services;


import de.eimantas.processing.clients.BookingsClient;
import de.eimantas.processing.clients.ProjectsClient;
import de.eimantas.processing.messaging.BookingSender;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;

@Service
public class BookingService {


  @Inject
  BookingsClient bookingsClient;

  @Inject
  ProjectsClient projectsClient;

  @Inject
  BookingSender bookingSender;

  private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

  // that fucking formatter will haunt me for a long time....
  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


  public void processBooking(long message) {
    logger.info("Booking received: " + message + " getting info for the booking");


    JSONObject booking = getBookingInfo(message);

    logger.info(booking.toString());

    long minutes = calculateHours(booking);
    int projectId = getProjectId(booking);

    logger.info("getting rate for project: " + projectId);
    BigDecimal rate = getRateFromProject(projectId);
    logger.info("rate is: " + rate);
    BigDecimal betrag = calculateAmount(minutes, rate);
    logger.info("betrag is: " + betrag);

    try {
      logger.debug("creating json");
      JSONObject json = new JSONObject();
      json.put("booking_id", message);
      json.put("Amount", betrag);
      sendNotification(json);
    } catch (JSONException e) {
      e.printStackTrace();
      logger.error("processed booking");
    }

  }


  public void sendNotification(JSONObject json) throws JSONException {
    bookingSender.sendProcessedNotification(json);
  }

  public BigDecimal calculateAmount(long minutes, BigDecimal rate) {

    BigDecimal ratePerMinute = rate.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
    logger.info("minute rate is: " + ratePerMinute);

    return ratePerMinute.multiply(BigDecimal.valueOf(minutes)).setScale(2, RoundingMode.HALF_UP);
  }

  public BigDecimal getRateFromProject(int projectId) {

    logger.info("receiving rate for project id: " + projectId);
    ResponseEntity response = projectsClient.getProject(projectId);
    logger.info("response message is: " + response.getBody().toString());


    JSONObject json = new JSONObject((LinkedHashMap) response.getBody());
    logger.info(json.toString());

    try {

      BigDecimal rate = new BigDecimal(json.getString("rate"));
      logger.info("Project rate is: " + rate);

      return rate;
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return BigDecimal.ZERO;
  }


  public long calculateHours(JSONObject booking) {

    try {
      String begin = booking.getString("startdate");
      String end = booking.getString("endDate");

      LocalDateTime beginTime = LocalDateTime.parse(begin, formatter);
      LocalDateTime endTime = LocalDateTime.parse(end, formatter);

      long minutesBetween = ChronoUnit.MINUTES.between(beginTime, endTime);

      logger.info("Difference between " + begin + " and " + end + " in minutes are " + minutesBetween);

      return minutesBetween;

    } catch (JSONException e) {
      e.printStackTrace();
    }
    return 0;

  }

  private int getProjectId(JSONObject booking) {

    try {
      int projectID = booking.getInt("projectId");
      logger.info("Project id is: " + projectID);
      return projectID;
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public JSONObject getBookingInfo(long bookingId) {
    ResponseEntity response = bookingsClient.getBookingById(bookingId);

    logger.info("response message is: " + response.getBody().toString());

    JSONObject json = new JSONObject((LinkedHashMap) response.getBody());

    return json;

  }

}
