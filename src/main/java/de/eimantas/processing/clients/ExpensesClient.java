package de.eimantas.processing.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "${feign.client.config.service.expenses}", configuration = BookingsClientConfig.class)
@RequestMapping(value = "/expense")
public interface ExpensesClient {


  // we need token in order to get resources in behalf of the user.
  // I dont want to create special "services" user because it is a danger that with "all knowing" user could be leaked.
  // this way we know that the message that is trying to get processed is allowed by the rousource
  //@Headers("Authorization: Bearer {access_token}")
  @GetMapping("/get/{id}")
  // ResponseEntity<?> getExpenseById(@Param("access_token") String token, @PathVariable(name = "id") long id);
  ResponseEntity<?> getExpenseById(@RequestHeader("Authorization") String token, @PathVariable(name = "id") long id);
}
