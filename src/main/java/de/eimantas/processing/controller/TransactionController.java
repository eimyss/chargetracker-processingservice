package de.eimantas.processing.controller;

import de.eimantas.processing.controller.expcetions.BadRequestException;
import de.eimantas.processing.entities.EntityTransaction;
import de.eimantas.processing.entities.types.EntityTransactionType;
import de.eimantas.processing.services.TransactionService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {

  @Autowired
  private TransactionService transactionService;

  private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


  @GetMapping("/all")
  @CrossOrigin(origins = "*")
  public List<EntityTransaction> getAllTransactions(Principal principal) {
    logger.info("find all transcations ");
    List<EntityTransaction> transactions = transactionService.getAllTransactions((KeycloakAuthenticationToken) principal);
    logger.info("got response with Size:  " + transactions.size());
    return transactions;
  }


  @GetMapping("/get/{id}")
  @CrossOrigin(origins = "*")
  public EntityTransaction getTransactionById(Principal principal, @PathVariable long id) {
    logger.info("find transcation for id " + id);
    EntityTransaction transaction = transactionService.getById(id, (KeycloakAuthenticationToken) principal);
    logger.info("returning some transaction: " + transaction.toString());
    return transaction;
  }

  @GetMapping("/get/{type}/{id}")
  @CrossOrigin(origins = "*")
  public EntityTransaction getTransactionById(Principal principal, @PathVariable(name = "type") String type, @PathVariable (name = "id") long id) throws BadRequestException {
    logger.info("find transcation for id " + id + " and type: " + type);


    EntityTransactionType transactionType;
    try {
      transactionType = EntityTransactionType.valueOf(type);
    } catch (Exception e) {
      logger.error("cannot determine type for string: " + type);
      throw new BadRequestException("cannot determine type");
    }

    logger.info("parsed type: " + transactionType);

    EntityTransaction transaction = transactionService.getByEntityId(id, transactionType, (KeycloakAuthenticationToken) principal);
    if ( transaction != null) {
      logger.info("returning some transaction: " + transaction.toString());
      return transaction;
    }
    logger.info("entity is not found!");
    throw new BadRequestException("Entity is not found!");

  }


  public ResponseEntity fallback(Throwable e) {
    logger.warn("faLLING BACK on get expenses");
    e.printStackTrace();
    logger.warn("failed to fallback", e);
    return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
