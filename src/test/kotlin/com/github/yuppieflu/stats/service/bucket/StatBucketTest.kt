package com.github.yuppieflu.stats.service.bucket

import com.github.yuppieflu.stats.service.domain.Measurement
import com.github.yuppieflu.stats.service.domain.Statistic
import com.github.yuppieflu.stats.util.StatAssert
import org.assertj.core.api.Assertions.offset
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.function.Consumer

class StatBucketTest {
    lateinit var bucket: StatBucket

    @Before
    fun setup() {
        bucket = StatBucket()
    }

    @Test
    fun testConcurrentAddMeasurements() {
        // setup
        val measurements = generateSequence(::getRandMeasurementFromOneBucket)
                .take(10000)
                .toList()
        val expectedStats = measurements.map(Measurement::toStat).reduce(Statistic::combine)

        val latch = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(10)

        val results = measurements.map { m -> CompletableFuture.runAsync(Runnable { addMeasurementWithLatch(m, latch) }, executor) }.toList()
        latch.countDown()
        results.forEach(Consumer { it.join() })

        // when
        val statistic = bucket.content().toStatistic()

        // then
        StatAssert.assertThat(statistic).isCloseTo(expectedStats, offset(0.01))
    }

    private fun addMeasurementWithLatch(m: Measurement, latch: CountDownLatch) {
        latch.await()
        bucket.addMeasurement(m)
    }
}