package de.eimantas.processing.messaging;

import de.eimantas.processing.services.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;


public class BookingReceiver {

  @Inject
  BookingService bookingService;

  private static final Logger logger = LoggerFactory.getLogger(BookingReceiver.class);

  public BookingReceiver() {

  }

  //  @RabbitListener(queues = "orderServiceQueue")
  public void receive(String message) {
    logger.info("Received message '{}'", message);
  }

  public void handleMessage(Object message) throws IOException {
    logger.info("Received message about created booking ID: '{}'", message);
    bookingService.processBooking((String)message);
  }


}
