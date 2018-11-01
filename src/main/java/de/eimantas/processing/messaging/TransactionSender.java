package de.eimantas.processing.messaging;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionSender {
  private final RabbitTemplate rabbitTemplate;

  private ObjectMapper mapper = new ObjectMapper();

  private final Exchange exchange;

  private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


  public TransactionSender(RabbitTemplate rabbitTemplate, Exchange exchange) {
    this.rabbitTemplate = rabbitTemplate;
    this.exchange = exchange;
    JavaTimeModule module = new JavaTimeModule();
    mapper.registerModule(module);
  }


  public void sendProcessedNotification(String expense) {
    String routingKey = "transaction.processed";
    logger.info("Sending to exchange: " + exchange.getName() + " with message: " + expense);
    rabbitTemplate.convertAndSend(exchange.getName(), routingKey, expense);
  }
}