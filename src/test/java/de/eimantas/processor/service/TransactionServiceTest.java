package de.eimantas.processor.service;

import de.eimantas.processing.ProceessingBackendApplication;
import de.eimantas.processing.entities.EntityTransaction;
import de.eimantas.processing.entities.types.EntityTransactionType;
import de.eimantas.processing.repo.TransactionRepository;
import de.eimantas.processing.services.TransactionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.mockito.Mockito;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProceessingBackendApplication.class)
@WebAppConfiguration
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class TransactionServiceTest {
  private static final String TEST_USER_ID = "9a204126-12b9-4efe-9d9b-3808aba51ba3";


  private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

  private MockMvc mockMvc;


  @Inject
  private TransactionService transactionService;

  @Inject
  private TransactionRepository transactionRepository;

  @Autowired
  private WebApplicationContext webApplicationContext;


  private KeycloakAuthenticationToken mockPrincipal;


  @Before
  public void setup() throws Exception {

    // auth stuff
    mockPrincipal = Mockito.mock(KeycloakAuthenticationToken.class);
    Mockito.when(mockPrincipal.getName()).thenReturn("test");

    KeycloakPrincipal keyPrincipal = Mockito.mock(KeycloakPrincipal.class);
    RefreshableKeycloakSecurityContext ctx = Mockito.mock(RefreshableKeycloakSecurityContext.class);

    AccessToken token = Mockito.mock(AccessToken.class);
    Mockito.when(token.getSubject()).thenReturn(TEST_USER_ID);
    Mockito.when(ctx.getToken()).thenReturn(token);
    Mockito.when(keyPrincipal.getKeycloakSecurityContext()).thenReturn(ctx);
    Mockito.when(mockPrincipal.getPrincipal()).thenReturn(keyPrincipal);

    transactionRepository.deleteAll();
    logger.info("Filling data");
    // For booking
    EntityTransaction transaction = new EntityTransaction();
    transaction.setAmountBefore(BigDecimal.ZERO);
    transaction.setAmountAfter(BigDecimal.TEN);
    transaction.setAccountId(1);
    transaction.setRefEntityId(1);
    transaction.setType(EntityTransactionType.BOOKING);
    transaction.setProcessingDate(LocalDateTime.now());
    transaction.setUserId(TEST_USER_ID);
    transactionRepository.save(transaction);


  }


  @Test
  public void testGetAllTransactions() throws Exception {

    List<EntityTransaction> transactions = transactionService.getAllTransactions(mockPrincipal);
    Assert.assertNotNull(transactions);
    assertThat(transactions.size()).isEqualTo(1);
  }

  @Test
  public void testGetByEntityId() throws Exception {

    EntityTransaction transaction = transactionService.getByEntityId(1, EntityTransactionType.BOOKING, mockPrincipal);
    Assert.assertNotNull(transaction);
    assertThat(transaction.getAmountAfter()).isNotNull();

  }

  @Test
  public void testGetTransactionWrongType() throws Exception {

    EntityTransaction transaction = transactionService.getByEntityId(1, EntityTransactionType.EXPENSE, mockPrincipal);
    Assert.assertNull(transaction);

  }


}
