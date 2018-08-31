package com.github.yuppieflu.stats.service.bucket

import com.github.yuppieflu.stats.service.StorageService
import com.github.yuppieflu.stats.service.domain.Measurement
import com.github.yuppieflu.stats.service.domain.Statistic
import com.github.yuppieflu.stats.service.domain.Status
import com.github.yuppieflu.stats.util.StatAssert
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.offset
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class BucketsStorageServiceTest {
    companion object {
        val VALUE = 1.0
        val FIXED_TS = Instant.now()
    }

    lateinit var storageService: StorageService

    @Before
    fun setup() {
        storageService = BucketsStorageService(Clock.fixed(FIXED_TS, ZoneOffset.UTC))
    }

    @Test
    fun testMillSecondsOfMinute() {
        // setup
        val epochMillis = LocalDateTime.parse("2007-12-03T10:15:49")
                .toInstant(ZoneOffset.UTC)
                .plusMillis(587)
                .toEpochMilli()

        val bucketStorageService = BucketsStorageService(Clock.systemUTC())

        // expect
        assertThat(bucketStorageService.milliSecondOfMinute(epochMillis)).isEqualTo(49587)
    }

    @Test
    fun invalidStatusForFutureTransaction() {
        // setup
        val m = measurement(FIXED_TS.plusMillis(1), VALUE)

        // when
        val status = storageService.addMeasurement(m)

        // then
        assertThat(status).isEqualTo(Status.INVALID)
    }

    @Test
    fun rejectedStatusForTooOldTransaction() {
        // setup
        val m = measurement(FIXED_TS.minusSeconds(60).minusMillis(1), VALUE)

        // when
        val status = storageService.addMeasurement(m)

        // then
        assertThat(status).isEqualTo(Status.REJECTED)
    }

    @Test
    fun tooOldMeasurementsAreIgnored() {
        // setup
        val m = measurement(FIXED_TS.minusSeconds(60).minusMillis(1), VALUE)
        storageService.addMeasurement(m)

        // when
        val statistic = storageService.getStatistic()

        // then
        assertThat(statistic).isEqualTo(empty())
    }

    @Test
    fun processedStatusForTransactionWithinMinute() {
        // setup
        val m = measurement(FIXED_TS.minusSeconds(60), VALUE)

        // when
        val status = storageService.addMeasurement(m)

        // then
        assertThat(status).isEqualTo(Status.PROCESSED)
    }

    @Test
    fun aggregationWithinOneBucketWorks() {
        // setup
        val expectedStats = generateSequence(this::getRandMeasurement)
                .take(3)
                .onEach { storageService.addMeasurement(it) }
                .map(Measurement::toStat)
                .reduce(Statistic::combine)

        // when
        val statistic = storageService.getStatistic()

        // then
        StatAssert.assertThat(statistic).isCloseTo(expectedStats, offset(0.01))
    }

    @Test
    fun aggregationWithinSeveralBucketsWorks() {
        // setup
        val expectedStats = sequenceOf(2, 33, 17, 2, 33, 2)
                .map(this::getRandMeasurementSecondsAgo)
                .onEach{ storageService.addMeasurement(it) }
                .map(Measurement::toStat)
                .reduce(Statistic::combine)

        // when
        val statistic = storageService.getStatistic()

        // then
        StatAssert.assertThat(statistic).isCloseTo(expectedStats, offset(0.01))
    }

    private fun getRandMeasurement() = getRandMeasurementSecondsAgo(5)

    private fun getRandMeasurementSecondsAgo(secondsAgo: Int) = getRandNotOldMeasurementFromBucket(FIXED_TS, secondsAgo)

    private fun measurement(instant: Instant, value: Double) = Measurement(instant.toEpochMilli(), value)
}