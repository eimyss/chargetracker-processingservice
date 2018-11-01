package de.eimantas.processing.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "${feign.client.config.service.booking}", configuration = BookingsClientConfig.class)
@RequestMapping(value = "/booking")
public interface BookingsClient {

  @GetMapping("/get/{id}")
  ResponseEntity<?> getBookingById(@RequestHeader("Authorization") String token, @PathVariable(name = "id") long id);

}
