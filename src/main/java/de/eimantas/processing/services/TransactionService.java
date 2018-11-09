package de.eimantas.processing.services;


import de.eimantas.processing.entities.EntityTransaction;
import de.eimantas.processing.entities.EntityTransactionError;
import de.eimantas.processing.entities.types.EntityTransactionType;
import de.eimantas.processing.repo.TransactionErrorRepository;
import de.eimantas.processing.repo.TransactionRepository;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class TransactionService {

  @Inject
  TransactionErrorRepository transactionErrorRepository;

  @Inject
  SecurityService securityService;

  @Inject
  TransactionRepository transactionRepository;


  private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);


  public List<EntityTransaction> getAllTransactions(KeycloakAuthenticationToken principal) {
    logger.debug("finding all transactions");
    return transactionRepository.findByUserId(securityService.getUserIdFromPrincipal(principal));
  }

  public List<EntityTransactionError> getAllErrorTransactions(KeycloakAuthenticationToken principal) {
    logger.debug("finding all error transactions");
    return transactionErrorRepository.findByUserId(securityService.getUserIdFromPrincipal(principal));
  }

  public EntityTransaction getById(long id, KeycloakAuthenticationToken principal) {
    logger.debug("finding transactions by Id");
    return transactionRepository.findByIdAndUserId(id, securityService.getUserIdFromPrincipal(principal));
  }

  public EntityTransactionError getErrorByEntityId(KeycloakAuthenticationToken principal, long id) {
    logger.debug("finding error transactions for entity ID: " + id);
    return transactionErrorRepository.findByUserIdAndRefEntityId(securityService.getUserIdFromPrincipal(principal), id);
  }

  public EntityTransaction getByEntityId(long id, EntityTransactionType type, KeycloakAuthenticationToken principal) {
    String user = securityService.getUserIdFromPrincipal(principal);
    logger.info("finding by ref entity id: " + id + " and type: " + type + " and user: " + user);
    return transactionRepository.findByRefEntityIdAndTypeAndUserId(id, type, user);

  }
}
