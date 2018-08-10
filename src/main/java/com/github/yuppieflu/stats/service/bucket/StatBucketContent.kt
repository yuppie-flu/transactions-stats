package com.github.yuppieflu.stats.service.bucket

import com.github.yuppieflu.stats.service.domain.Statistic
import kotlin.math.max
import kotlin.math.min

class StatBucketContent(val count: Long, val min: Double, val max: Double, val sum: Double) {

    fun toStatistic() = Statistic(count = this.count, min = this.min, max = this.max, sum = this.sum)

    companion object {
        val EMPTY = StatBucketContent(count = 0, min = 0.0, max = 0.0, sum = 0.0)
        fun add(a: StatBucketContent, b: StatBucketContent) =
                StatBucketContent(count = a.count + b.count, min = min(a.min, b.min), max = max(a.max, b.max), sum = a.sum + b.sum)
    }
}