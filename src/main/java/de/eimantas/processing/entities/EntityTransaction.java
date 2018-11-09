package de.eimantas.processing.entities;


import de.eimantas.processing.entities.types.EntityTransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class EntityTransaction {

  @Id
  @GeneratedValue
  private long id;
  private EntityTransactionType type;
  private BigDecimal amountBefore;
  private BigDecimal amountAfter;
  private LocalDateTime processingDate;
  private boolean canceled;
  private long refEntityId;
  private long accountId;
  private LocalDateTime cancellingDate;
  private @NonNull
  String userId;
}
