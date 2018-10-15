package de.eimantas.processing.config;

import de.eimantas.processing.messaging.BookingSender;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingSenderConfiguration {


  @Bean
  public Exchange eventExchange() {
    return new TopicExchange("eventExchange");
  }

  @Bean
  public BookingSender expensesSender(RabbitTemplate rabbitTemplate, Exchange eventExchange) {
    return new BookingSender(rabbitTemplate, eventExchange);
  }

}
