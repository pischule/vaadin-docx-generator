package by.bsu.pischule.generator;

import by.bsu.pischule.model.Parameters;
import by.bsu.pischule.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class TransactionGenerator {
    private final Random random = new Random();
    private final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private final List<String> CURRENCIES = List.of(
            "BYN", "USD", "EUR", "UAH", "KPW", "VEF", "ZWD", "KZT"
    );

    public List<Transaction> generateTransactions(Parameters parameters) {
        int startId = random.nextInt(10000);
        long daysDelta = ChronoUnit.DAYS.between(parameters.getDateFrom(), parameters.getDateTo());

        List<Transaction> transactionList = new ArrayList<>();
        int n = parameters.getRowCount();
        for (int i = 0; i < n; ++i) {
            long id = startId + i;
            BigDecimal amount = new BigDecimal(random.nextInt(100))
                    .pow(3)
                    .divide(ONE_HUNDRED, 2, RoundingMode.HALF_EVEN);
            String description = "description" + random.nextInt(100);
            String currency = CURRENCIES.get(random.nextInt(CURRENCIES.size()));
            LocalDate date = parameters.getDateFrom().plusDays((i + 1) * daysDelta / n);
            Transaction transaction = Transaction.builder()
                    .date(date)
                    .currency(currency)
                    .amount(amount)
                    .id(id)
                    .date(date)
                    .description(description)
                    .build();
            transactionList.add(transaction);
        }

        return transactionList;
    }
}
