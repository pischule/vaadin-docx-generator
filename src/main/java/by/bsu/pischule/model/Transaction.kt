package by.bsu.pischule.model

import java.math.BigDecimal
import java.time.LocalDate

data class Transaction(
    val id: Long,
    val date: LocalDate,
    val description: String,
    val currency: String,
    val amount: BigDecimal,
)