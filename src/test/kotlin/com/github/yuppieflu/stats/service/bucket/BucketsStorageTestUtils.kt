package com.github.yuppieflu.stats.service.bucket

import com.github.yuppieflu.stats.rest.dto.Transaction
import com.github.yuppieflu.stats.service.domain.Measurement
import com.github.yuppieflu.stats.service.domain.Stat
import com.github.yuppieflu.stats.service.domain.Statistic
import java.time.Instant
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max
import kotlin.math.min

private val FIXED_TS = Instant.now()
private val MAX_MEASUREMENT_VALUE = 10.0
private val DEF_SECONDS_AGO = 5

fun getRandMeasurementFromOneBucket(): Measurement {
    return getRandNotOldMeasurementFromBucket(FIXED_TS, DEF_SECONDS_AGO)
}

fun getRandNotOldMeasurementFromBucket(instant: Instant, secondsAgo: Int): Measurement {
    val timestamp = instant.minusSeconds(secondsAgo.toLong()).toEpochMilli()
    return Measurement(timestamp, ThreadLocalRandom.current().nextDouble(MAX_MEASUREMENT_VALUE))
}

fun empty() = Statistic(count = 0, min = 0.0, max = 0.0, sum = 0.0)

fun Statistic.combine(stat: Stat) =
        Statistic(count = this.count + stat.count, min = min(this.min, stat.min), max = max(this.max, stat.max), sum = this.sum + stat.sum)

fun Measurement.toStat() = Statistic(count = 1, min = this.value, max = this.value, sum = this.value)

fun Transaction.toStat() = Statistic(count = 1, min = this.amount, max = this.amount, sum = this.amount)
