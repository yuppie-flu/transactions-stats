package com.github.yuppieflu.stats.rest.dto

import com.github.yuppieflu.stats.service.domain.Stat

class Statistic(
        override val count: Long,
        override val min: Double,
        override val max: Double,
        override val sum: Double
) : Stat {
    constructor(stat: Stat) : this(count = stat.count, min = stat.min, max = stat.max, sum = stat.sum)
}