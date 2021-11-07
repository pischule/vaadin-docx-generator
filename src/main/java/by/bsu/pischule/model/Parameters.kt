package by.bsu.pischule.model

import java.time.LocalDate
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class Parameters(
    var name: @NotEmpty(message = "имя должно быть не пустым") String? = null,
    var account: @NotNull @Min(value = 1, message = "номера аккаунтов начинаются с 1") Int? = null,
    var dateFrom: @NotNull(message = "дата должна быть непустой") LocalDate = LocalDate.now(),
    var dateTo: @NotNull(message = "дата должна быть непустой") LocalDate = LocalDate.now(),
    var allCaps: Boolean = false,
    var rowCount: @NotNull @Min(
        value = 0,
        message = "число строк не может быть отрицательным"
    ) @Max(value = 10000, message = "слишком много строк") Int? = null,
    var template: ByteArray? = null
)