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


  @Value("${processing.messaging.subscriber.routingKey}")
  private String routingKey;

  @Value("${processing.messaging.exchange}")
  private String eventexchange;

  @Value("${processing.messaging.orderServiceQueueName}")
  private String orderQueue;

  @Value("${processing.messaging.bookingServiceQueueName}")
  private String bookingQueue;


  @Bean
  public TopicExchange eventExchange() {
    return new TopicExchange(eventexchange);
  }

  @Bean
  public Queue queue() {
    return new Queue(orderQueue);
  }

  @Bean
  public Queue bookingQueue() {
    return new Queue(bookingQueue);
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
    return BindingBuilder.bind(bookingQueue()).to(eventExchange()).with("booking.created");
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
                                           @Qualifier("listenerAdapter") MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(orderQueue);
    container.setMessageListener(listenerAdapter);
    return container;
  }

  @Bean
  SimpleMessageListenerContainer bookingContainer(ConnectionFactory connectionFactory,
                                                  @Qualifier("bookingListenerAdapter") MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(bookingQueue);
    container.setMessageListener(listenerAdapter);
    return container;
  }


}
