package de.eimantas.processing.entities.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(description = "")
public class ExpenseDTO {


        private Long id;
        private
        String name;
        private String ort;
        Long userId;
        private Instant createDate;
        private boolean expensed;
        private boolean expensable;
        private boolean valid;
        private boolean periodic;
        Long accountId;
        private BigDecimal betrag;
        private String category;
    }

