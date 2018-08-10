package com.github.yuppieflu.stats.service.domain

data class Statistic(
        override val count: Long,
        override val min: Double,
        override val max: Double,
        override val sum: Double
) : Stat