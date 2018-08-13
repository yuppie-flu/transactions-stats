package com.github.yuppieflu.stats.service.bucket

import com.github.yuppieflu.stats.service.StorageService
import com.github.yuppieflu.stats.service.domain.Measurement
import com.github.yuppieflu.stats.service.domain.Statistic
import com.github.yuppieflu.stats.service.domain.Status
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoField

class BucketsStorageService(private val clock: Clock): StorageService {

    companion object {
        const val SIZE = 60_000
    }

    private val buckets = generateSequence(::StatBucket).take(SIZE).toList()

    override fun addMeasurement(m: Measurement): Status {
        val measurementTs = m.timestamp
        val millisInPast = clock.millis() - measurementTs
        return when {
            millisInPast < 0 -> Status.INVALID
            millisInPast > SIZE -> Status.REJECTED
            else ->  {
                val bucketIndex = milliSecondOfMinute(measurementTs)
                buckets[bucketIndex].addMeasurement(m)
                Status.PROCESSED
            }
        }
    }

    fun milliSecondOfMinute(epochMillis: Long): Int {
        val localDateTime = LocalDateTime.ofEpochSecond(
                epochMillis / 1000, 0, ZoneOffset.UTC)
        val millis = (epochMillis % 1000).toInt()
        return localDateTime.get(ChronoField.SECOND_OF_MINUTE) * 1000 + millis
    }

    override fun getStatistic(): Statistic {
        val requestTimestamp = clock.millis()
        val sequence = buckets.asSequence()
                .filter { b -> b.isWithin60SecondsFrom(requestTimestamp) }
                .map(StatBucket::content)

        val value = if (sequence.none())
            StatBucketContent.EMPTY
        else
            sequence.reduce(StatBucketContent::add)

        return value.toStatistic()

    }
}