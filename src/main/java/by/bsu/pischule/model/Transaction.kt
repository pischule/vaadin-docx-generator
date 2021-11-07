package by.bsu.pischule.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction {
    private Long id;
    private LocalDate date;
    private String description;
    private String currency;
    private BigDecimal amount;
}
