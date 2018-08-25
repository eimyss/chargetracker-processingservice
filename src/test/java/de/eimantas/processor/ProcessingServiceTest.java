package de.eimantas.processor;


import de.eimantas.processing.ProceessingBackendApplication;
import de.eimantas.processing.data.processing.DataProcessor;
import de.eimantas.processing.entities.dto.ExpenseDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProceessingBackendApplication.class)
@WebAppConfiguration
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProcessingServiceTest {
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	private DataProcessor dataProcessor;

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;



    @Before
	public void setup() throws Exception {

		this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void dateFormatParse() throws Exception {


  String date = "24.08.2018";
  SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

  Date datum = df.parse(date);


        assertThat(datum).isNotNull();
        logger.info("Date: " +datum.toString());

    }

	@Test
	public void testReadData() throws Exception {

    	List<ExpenseDTO> dtos = dataProcessor.loadObjectList(ExpenseDTO.class, "data.csv");
    	
        assertThat(dtos).isNotNull();
		logger.info("size: " +dtos.size());

	}


}
