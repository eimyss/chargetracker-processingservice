package de.eimantas.processor.service;

import de.eimantas.processing.ProceessingBackendApplication;
import de.eimantas.processing.services.BookingService;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
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
public class BookingServiceTest {


  private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

  private MockMvc mockMvc;

  String booking = "{id=1, name=populated, startdate=2018-10-14 13:25:39, endDate=2018-10-14 17:15:39, projectId=2, serverBookingId=11, userId=ee9fb974-c2c2-45f8-b60e-c22d9f00273f}";
  JSONObject json;

  @Inject
  private BookingService bookingService;

  @Autowired
  private WebApplicationContext webApplicationContext;


  private KeycloakAuthenticationToken mockPrincipal;


  @Before
  public void setup() throws Exception {
    LinkedHashMap<String, String> map = new LinkedHashMap();
    map.put("id", "1");
    map.put("name", "populated");
    map.put("startdate", "2018-10-14 13:25:39");
    map.put("endDate", "2018-10-14 17:15:39");
    map.put("projectId", "2");
    map.put("serverBookingId", "11");
    map.put("userId", "ee9fb974-c2c2-45f8-b60e-c22d9f00273f");
    this.mockMvc = webAppContextSetup(webApplicationContext).build();
    json = new JSONObject(map);
  }


  @Test
  public void testGetProjectRate() throws Exception {

    BigDecimal rate = bookingService.getRateFromProject(2);
    Assert.assertThat(rate, Matchers.greaterThan(BigDecimal.ZERO));

  }

  @Test
  public void testCalculateHours() throws Exception {

    long minutes = bookingService.calculateHours(json);
    Assert.assertThat(minutes, Matchers.is(230L));
    logger.info("time was: " + minutes);

  }

  @Test
  public void testCalculateWholeAmount() throws Exception {

    BigDecimal rate = new BigDecimal(85);

    BigDecimal amount = bookingService.calculateAmount(230,rate);
    Assert.assertThat(amount, Matchers.is(new BigDecimal("326.60")));
    logger.info("time was: " + amount);

  }


  @Test
  public void testGetProject() throws Exception {

    JSONObject booking = bookingService.getBookingInfo(11L);
    Assert.assertNotNull(booking);
    logger.info("Booking: " + booking.toString());
  }


}
