package com.github.yuppieflu.stats.service.domain

interface Stat {
    val count: Long
    val min: Double
    val max: Double
    val sum: Double
    val avg: Double
        get() = if (count == 0L) 0.0 else sum / count
}

