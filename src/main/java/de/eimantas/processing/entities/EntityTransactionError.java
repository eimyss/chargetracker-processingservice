package de.eimantas.processing.entities;


import de.eimantas.processing.entities.types.EntityTransactionType;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
public class EntityTransactionError {

  @Id
  @GeneratedValue
  private long id;
  private EntityTransactionType type;
  @Lob
  @Column
  private String message;
  @Lob
  @Column
  private String exception;
  private Instant date;
}
