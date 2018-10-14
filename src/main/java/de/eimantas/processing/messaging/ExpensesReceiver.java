package de.eimantas.processing.messaging;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.hal.Jackson2HalModule;

import java.io.IOException;


public class ExpensesReceiver {

    private static final Logger logger = LoggerFactory.getLogger(ExpensesReceiver.class);


    private ObjectMapper customMapper;


    public ExpensesReceiver() {
         customMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .registerModule(new Jackson2HalModule());


    }

    //  @RabbitListener(queues = "orderServiceQueue")
    public void receive(String message) {
        logger.info("Received message '{}'", message);
    }

    public void handleMessage(Object message) throws IOException {
        customMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        logger.info("Hangle message '{}'", message);
       // ExpenseDTO dto= customMapper.readValue((String)message, ExpenseDTO.class);
     //   logger.info("Converted Name: " + dto.getName());

    }


}
