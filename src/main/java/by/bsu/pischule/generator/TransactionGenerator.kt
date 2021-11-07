package by.bsu.pischule.generator

import by.bsu.pischule.model.Parameters
import by.bsu.pischule.model.Transaction
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class TransactionGenerator {
    private val random = Random()
    private val oneHundred = BigDecimal("100")
    private val currencies = listOf(
        "BYN", "USD", "EUR", "UAH", "KPW", "VEF", "ZWD", "KZT"
    )

    fun generateTransactions(parameters: Parameters): List<Transaction> {
        val startId = random.nextInt(10000)
        val daysDelta = ChronoUnit.DAYS.between(parameters.dateFrom, parameters.dateTo)
        val transactionList: MutableList<Transaction> = mutableListOf()
        val n = parameters.rowCount ?: 0
        for (i in 0 until n) {
            val id = (startId + i).toLong()
            val amount = BigDecimal(random.nextInt(100))
                .pow(3)
                .divide(oneHundred, 2, RoundingMode.HALF_EVEN)
            val description = "description" + random.nextInt(100)
            val currency = currencies[random.nextInt(currencies.size)]
            val date = parameters.dateFrom.plusDays((i + 1) * daysDelta / n)
            val transaction = Transaction(
                id = id,
                date = date,
                description = description,
                currency = currency,
                amount = amount
            )
            transactionList.add(transaction)
        }
        return transactionList
    }
}