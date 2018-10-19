package de.eimantas.processing.repo;

import de.eimantas.processing.entities.EntityTransaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<EntityTransaction, Long> {


}