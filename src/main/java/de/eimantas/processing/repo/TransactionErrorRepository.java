package de.eimantas.processing.repo;

import de.eimantas.processing.entities.EntityTransactionError;
import org.springframework.data.repository.CrudRepository;

public interface TransactionErrorRepository extends CrudRepository<EntityTransactionError, Long> {


}