package de.eimantas.processing.config;

import de.eimantas.processing.messaging.BookingReceiver;
import de.eimantas.processing.messaging.ExpensesReceiver;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfiguration {

  @Value("${subscriber.queue}")
  private String queueName;

  @Value("${subscriber.routingKey}")
  private String routingKey;

  @Bean
  public TopicExchange eventExchange() {
    return new TopicExchange("eventExchange");
  }

  @Bean
  public Queue queue() {
    return new Queue("orderServiceQueue");
  }

  @Bean
  public Queue bookingQueue() {
    return new Queue("bookingServiceQueue");
  }


  @Bean
  public Binding expensesBinding() {
    if (routingKey == null) {
      throw new IllegalStateException("No events to listen to! Please specify the routing key for the events to listen to with the property 'subscriber.routingKey' (see EventPublisher for available routing keys).");
    }
    return BindingBuilder
        .bind(queue())
        .to(eventExchange())
        .with(routingKey);
  }

  @Bean
  Binding bookingsBinding() {
    return BindingBuilder.bind(bookingQueue()).to(eventExchange()).with("#");
  }

  @Bean
  public ExpensesReceiver eventReceiver() {
    return new ExpensesReceiver();
  }

  @Bean
  public BookingReceiver bookingReceiver() {
    return new BookingReceiver();
  }


  @Bean
  public MessageListenerAdapter listenerAdapter(ExpensesReceiver receiver) {
    return new MessageListenerAdapter(receiver);
  }

  @Bean
  public MessageListenerAdapter bookingListenerAdapter(BookingReceiver receiver) {
    return new MessageListenerAdapter(receiver);
  }


  @Bean
  SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                           @Qualifier("listenerAdapter")MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(queueName);
    container.setMessageListener(listenerAdapter);
    return container;
  }

  @Bean
  SimpleMessageListenerContainer bookingContainer(ConnectionFactory connectionFactory,
                                                  @Qualifier("bookingListenerAdapter") MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames("bookingServiceQueue");
    container.setMessageListener(listenerAdapter);
    return container;
  }


}
