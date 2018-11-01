package de.eimantas.processing.entities;


import de.eimantas.processing.entities.types.EntityTransactionType;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

@Entity
@Data
public class EntityTransactionError {

  @Id
  @GeneratedValue
  private long id;
  private EntityTransactionType type;
  private String message;
  private String exception;
  private Instant date;
}
