package de.eimantas.processing.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


public class ExpensesService {

    private static final Logger logger = LoggerFactory.getLogger(ExpensesService.class);


  //  @RabbitListener(queues = "orderServiceQueue")
    public void receive(String message) {
        logger.info("Received message '{}'", message);
    }

    public void handleMessage(Object message) {
        logger.info("Received message '{}'", message);
    }






}
