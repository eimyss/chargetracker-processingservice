package de.eimantas.processing.data.processing;

import de.eimantas.processing.entities.EntityTransaction;
import de.eimantas.processing.entities.types.EntityTransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class ProcessingHelper {

  private static int taxesProcent = 19;

  public static EntityTransaction createInitTransaction(EntityTransactionType type, long entityId) {

    EntityTransaction transaction = new EntityTransaction();
    transaction.setType(type);
    transaction.setRefEntityId(entityId);
    transaction.setProcessingDate(LocalDateTime.now());

    return transaction;
  }


  public static BigDecimal calculateNetto(BigDecimal amount) {
    // forgive me for 2nd grade maths....
    int nettoPercent = 100 - taxesProcent;
    BigDecimal percentRate = amount.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    return percentRate.multiply(BigDecimal.valueOf(nettoPercent)).setScale(2, RoundingMode.HALF_UP);
  }


}
