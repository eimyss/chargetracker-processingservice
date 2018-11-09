package de.eimantas.processing.repo;

import de.eimantas.processing.entities.EntityTransaction;
import de.eimantas.processing.entities.types.EntityTransactionType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<EntityTransaction, Long> {

  List<EntityTransaction> findByUserId(String userId);

  EntityTransaction findByIdAndUserId(long id, String userId);

  EntityTransaction findByRefEntityIdAndTypeAndUserId(long id, EntityTransactionType type, String userIdFromPrincipal);
}