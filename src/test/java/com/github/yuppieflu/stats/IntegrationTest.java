package com.github.yuppieflu.stats;

import com.github.yuppieflu.stats.rest.dto.Statistic;
import com.github.yuppieflu.stats.rest.dto.Transaction;
import com.github.yuppieflu.stats.util.StatAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.summarizingDouble;
import static org.assertj.core.api.Assertions.offset;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    private static final double MAX_TRANSACTION_AMOUNT = 100;
    private static final int TRANSACTIONS_COUNT = 50;
    private static final long MAX_DELAY_MILLIS = 200;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    public void addSeveralTransactionsTest() {
        // setup
        List<Transaction> transactionList = Stream.generate(this::createTransaction)
                                                  .limit(TRANSACTIONS_COUNT)
                                                  .collect(Collectors.toList());

        DoubleSummaryStatistics expectedStat = transactionList.stream()
                                                            .collect(summarizingDouble(Transaction::getAmount));
        transactionList.forEach(this::postTransactionWithDelay);

        // when
        Statistic stat = testRestTemplate.getForObject("/statistics", Statistic.class);

        // then
        StatAssert.assertThat(stat).isCloseTo(expectedStat, offset(0.01));
    }

    private void postTransactionWithDelay(Transaction transaction) {
        try {
            testRestTemplate.postForEntity("/transactions", transaction, Void.class);
            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextLong(MAX_DELAY_MILLIS));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Transaction createTransaction() {
        double randomAmount = ThreadLocalRandom.current().nextDouble(MAX_TRANSACTION_AMOUNT);
        double amount = BigDecimal.valueOf(randomAmount).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        return new Transaction(System.currentTimeMillis(), amount);
    }
}
