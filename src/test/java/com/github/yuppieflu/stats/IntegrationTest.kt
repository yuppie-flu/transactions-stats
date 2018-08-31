package com.github.yuppieflu.stats

import com.github.yuppieflu.stats.rest.dto.Statistic
import com.github.yuppieflu.stats.rest.dto.Transaction
import com.github.yuppieflu.stats.service.bucket.combine
import com.github.yuppieflu.stats.service.bucket.toStat
import com.github.yuppieflu.stats.util.StatAssert
import org.assertj.core.api.Assertions.offset
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    companion object {
        private const val MAX_TRANSACTION_AMOUNT = 100.0
        private const val TRANSACTIONS_COUNT = 50
        private const val MAX_DELAY_MILLIS: Long = 200
    }

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun addSeveralTransactionsTest() {
        // setup
        val transactionList = generateSequence(this::createTransaction)
                .take(TRANSACTIONS_COUNT)
                .toList()

        val expectedStat = transactionList
                .map(Transaction::toStat)
                .reduce(com.github.yuppieflu.stats.service.domain.Statistic::combine)

        transactionList.forEach(Consumer { this.postTransactionWithDelay(it) })

        // when
        val stat = testRestTemplate.getForObject("/statistics", Statistic::class.java)

        // then
        StatAssert.assertThat(stat).isCloseTo(expectedStat, offset(0.01))
    }

    private fun postTransactionWithDelay(transaction: Transaction) {
        testRestTemplate.postForEntity("/transactions", transaction, Void::class.java)
        TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextLong(MAX_DELAY_MILLIS))
    }

    private fun createTransaction(): Transaction {
        val randomAmount = ThreadLocalRandom.current().nextDouble(MAX_TRANSACTION_AMOUNT)
        val amount = BigDecimal.valueOf(randomAmount).setScale(2, RoundingMode.HALF_DOWN).toDouble()
        return Transaction(System.currentTimeMillis(), amount)
    }
}