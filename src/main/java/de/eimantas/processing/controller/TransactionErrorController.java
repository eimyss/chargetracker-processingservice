package de.eimantas.processing.controller;

import de.eimantas.processing.entities.EntityTransactionError;
import de.eimantas.processing.services.TransactionService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/transaction/error/")
public class TransactionErrorController {


  private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private TransactionService transactionService;

  @GetMapping("/get/entity/{id}")
  @CrossOrigin(origins = "*")
  public EntityTransactionError getErrorForEntityId(Principal principal, @PathVariable long id) {
    logger.info("get error for entity Id: " + id);
    EntityTransactionError response = transactionService.getErrorByEntityId((KeycloakAuthenticationToken) principal, id);
    logger.info("expenses count: " + response.toString());
    return response;
  }

  @GetMapping("/get/all")
  @CrossOrigin(origins = "*")
  public List<EntityTransactionError> getAllErrors(Principal principal) {
    logger.info("get all errors");
    List<EntityTransactionError> response = transactionService.getAllErrorTransactions((KeycloakAuthenticationToken) principal);
    logger.info("got size: " + response.size());
    return response;
  }

  public ResponseEntity fallback(Throwable e) {
    logger.warn("faLLING BACK on get expenses");
    e.printStackTrace();
    logger.warn("failed to fallback", e);
    return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
