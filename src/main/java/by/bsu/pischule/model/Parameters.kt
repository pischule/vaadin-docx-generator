package by.bsu.pischule.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Parameters {
    @NotEmpty(message = "имя должно быть не пустым")
    private String name;
    @NotNull
    @Min(value = 1, message = "номера аккаунтов начинаются с 1")
    private Integer account;
    @NotNull(message = "дата должна быть непустой")
    private LocalDate dateFrom;
    @NotNull(message = "дата должна быть непустой")
    private LocalDate dateTo;
    private Boolean allCaps;
    @NotNull
    @Min(value = 0, message = "число строк не может быть отрицательным")
    @Max(value = 10000, message = "слишком много строк")
    private Integer rowCount;
    byte[] template;
}
