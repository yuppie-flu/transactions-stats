package com.github.yuppieflu.stats.service

import com.github.yuppieflu.stats.service.domain.Measurement
import com.github.yuppieflu.stats.service.domain.Statistic
import com.github.yuppieflu.stats.service.domain.Status

interface StorageService {
    fun addMeasurement(m: Measurement) : Status
    fun getStatistic(): Statistic
}