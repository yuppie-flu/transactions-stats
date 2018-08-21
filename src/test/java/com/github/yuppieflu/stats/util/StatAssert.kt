package com.github.yuppieflu.stats.util

import com.github.yuppieflu.stats.service.domain.Stat
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import org.assertj.core.data.Offset
import java.util.*

class StatAssert(stat: Stat): AbstractAssert<StatAssert, Stat>(stat, StatAssert::class.java) {

    companion object {
        fun assertThat(stat: Stat) = StatAssert(stat)
    }

    fun isCloseTo(summaryStatistics: DoubleSummaryStatistics, offset: Offset<Double>): StatAssert {
        Assertions.assertThat(actual.count).isEqualTo(summaryStatistics.count)
        Assertions.assertThat(actual.max).isEqualTo(summaryStatistics.max)
        Assertions.assertThat(actual.min).isEqualTo(summaryStatistics.min)
        Assertions.assertThat(actual.sum).isCloseTo(summaryStatistics.sum, offset)
        Assertions.assertThat(actual.avg).isCloseTo(summaryStatistics.average, offset)
        return this
    }
}