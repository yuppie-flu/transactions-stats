package com.github.yuppieflu.stats.service.bucket

import com.github.yuppieflu.stats.service.domain.Statistic
import kotlin.math.max
import kotlin.math.min

class StatBucketContent(val count: Long, val min: Double, val max: Double, val sum: Double) {

    fun toStatistic() = Statistic(count = this.count, min = this.min, max = this.max, sum = this.sum)

    fun add(other: StatBucketContent) =
            StatBucketContent(count = count + other.count, min = min(min, other.min), max = max(max, other.max), sum = sum + other.sum)

    companion object {
        val EMPTY = StatBucketContent(count = 0, min = 0.0, max = 0.0, sum = 0.0)
    }
}