package de.eimantas.processor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.eimantas.processing.ProceessingBackendApplication;
import de.eimantas.processing.data.processing.ProcessingHelper;
import de.eimantas.processing.entities.EntityTransaction;
import de.eimantas.processing.entities.types.EntityTransactionType;
import de.eimantas.processing.messaging.TransactionSender;
import de.eimantas.processing.services.ExpensesProcessingService;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProceessingBackendApplication.class)
@WebAppConfiguration
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ExpensesServiceTest {


  private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

  private MockMvc mockMvc;

  String notificationBody = "{\"id\":1,\"type\":\"EXPEN\",\"amountBefore\":10.0,\"amountAfter\":8.10,\"processingDate\":null,\"canceled\":false,\"refEntityId\":10,\"accountId\":1,\"cancellingDate\":null}";

  JSONObject json;

  @Inject
  TransactionSender transactionSender;

  @Inject
  private ExpensesProcessingService expensesProcessingService;

  @Autowired
  private WebApplicationContext webApplicationContext;


  private KeycloakAuthenticationToken mockPrincipal;


  @Before
  public void setup() throws Exception {
    LinkedHashMap<String, String> map = new LinkedHashMap();
    map.put("id", "1");
    map.put("name", "populated");
    map.put("purpose", "Generated by the system");
    map.put("createDate", "2017-12-19T15:41:24.644Z");
    map.put("betrag", "10.0");
    map.put("accountId", "1");
    map.put("category", "STEUER");
    map.put("userId", "ee9fb974-c2c2-45f8-b60e-c22d9f00273f");
    this.mockMvc = webAppContextSetup(webApplicationContext).build();
    json = new JSONObject(map);
  }


  @Test
  public void testGetExpensesAmount() throws Exception {
    BigDecimal amount = expensesProcessingService.getExpenseAmount(json);
    Assert.assertNotNull(amount);
    Assert.assertEquals(amount, BigDecimal.valueOf(10.0));

  }


  @Test
  public void testCalculateNetto() throws Exception {

    BigDecimal netto = ProcessingHelper.calculateNetto(BigDecimal.TEN);
    Assert.assertNotNull(netto);
    Assert.assertEquals(netto, new BigDecimal("8.10"));
  }

  @Test
  @Ignore
  public void testAccountNotification() throws Exception {

    EntityTransaction transaction = ProcessingHelper.createInitTransaction(EntityTransactionType.EXPENSE, 10);
    transaction.setAccountId(1);
    transaction.setAmountBefore(BigDecimal.TEN);
    transaction.setId(1);
    transaction.setAmountAfter(new BigDecimal("8.10"));
    ObjectMapper mapper = new ObjectMapper();
    String body = mapper.writeValueAsString(transaction);
    logger.info("Sending body: " + body);

    transactionSender.sendProcessedNotification(body);
  }


}
