package de.eimantas.processing.entities.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import de.eimantas.processing.data.processing.Deserializer.CustomLBigDecimalDeserializer;
import de.eimantas.processing.data.processing.Deserializer.CustomLocalDateDeSerializer;
import de.eimantas.processing.data.processing.Deserializer.CustomLocalDateSerializer;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(description = "")
public class ExpenseDTO {


        private Long id;
        private
        String name;
        private String ort;
        private String purpose;
        Long userId;
        @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        private LocalDate createDate;
        @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        private LocalDate bookingDate;
        private boolean expensed;
        private boolean expensable;
        private boolean valid;
        private boolean periodic;
        Long accountId;
        @JsonDeserialize(using = CustomLBigDecimalDeserializer.class)
        private BigDecimal amount;
        private String currency;
        private String category;
        private boolean processed;
        private LocalDate processedDate;
    }

