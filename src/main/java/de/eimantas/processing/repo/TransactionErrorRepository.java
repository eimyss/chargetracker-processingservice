package de.eimantas.processing.repo;

import de.eimantas.processing.entities.EntityTransactionError;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionErrorRepository extends CrudRepository<EntityTransactionError, Long> {
  List<EntityTransactionError> findByUserId(String userIdFromPrincipal);

  EntityTransactionError findByUserIdAndRefEntityId(String userIdFromPrincipal, long id);
}