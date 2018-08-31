package com.github.yuppieflu.stats.service.bucket

import com.github.yuppieflu.stats.service.StorageService
import com.github.yuppieflu.stats.service.domain.Measurement
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class BucketsStorageServiceClockMovesTest {

    val clock = ManagedClock(Clock.systemUTC())
    lateinit var storageService: StorageService
    lateinit var now: Instant

    @Before
    fun setup() {
        storageService = BucketsStorageService(clock)
        now = Instant.now()
    }

    @Test
    fun valuesMoreThat60SecOldAreNotIncludedInStat() {
        // setup
        val mTime = now.minusSeconds(60).minusMillis(1)

        moveClock(now.minusSeconds(30))
        storageService.addMeasurement(measurement(mTime, 1.0))
        moveClock(now)

        // when
        val statistic = storageService.getStatistic()

        // then
        assertThat(statistic.count).isEqualTo(0)
    }

    @Test
    fun values60SecOldAreIncludedInStat() {
        // setup
        val mTime= now.minusSeconds(60)

        moveClock(now.minusSeconds(30))
        storageService.addMeasurement(measurement(mTime, 1.0))
        moveClock(now)

        // when
        val statistic = storageService.getStatistic()

        // then
        assertThat(statistic.count).isEqualTo(1)
    }

    @Test
    fun newValuesOverrideOldValuesFromTheSameBucket() {
        // setup
        val oldMTime = now.minusSeconds(60)
        val oldValue = 1.0
        val newValue = 2.0

        moveClock(now.minusSeconds(30))
        storageService.addMeasurement(measurement(oldMTime, oldValue))
        moveClock(now)

        storageService.addMeasurement(measurement(now, newValue))

        // when
        val statistic = storageService.getStatistic()

        // then
        assertThat(statistic.count).isEqualTo(1)
        assertThat(statistic.sum).isEqualTo(newValue)
    }

    fun moveClock(instant: Instant) {
        clock.clock = Clock.fixed(instant, ZoneOffset.UTC)
    }

    fun measurement(instant: Instant, value: Double) = Measurement(instant.toEpochMilli(), value)
}