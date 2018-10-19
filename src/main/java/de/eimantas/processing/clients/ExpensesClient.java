package de.eimantas.processing.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "${feign.client.config.service.expenses}", configuration = BookingsClientConfig.class)
@RequestMapping(value = "/expense")
public interface ExpensesClient {

  @GetMapping("/get/{id}")
  ResponseEntity<?> getExpenseById(@PathVariable(name = "id") long id);

}
