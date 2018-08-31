package com.github.yuppieflu.stats.util

import com.github.yuppieflu.stats.service.domain.Stat
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import org.assertj.core.data.Offset

class StatAssert(stat: Stat): AbstractAssert<StatAssert, Stat>(stat, StatAssert::class.java) {

    companion object {
        fun assertThat(stat: Stat) = StatAssert(stat)
    }

    fun isCloseTo(stat: Stat, offset: Offset<Double>): StatAssert {
        Assertions.assertThat(actual.count).isEqualTo(stat.count)
        Assertions.assertThat(actual.max).isEqualTo(stat.max)
        Assertions.assertThat(actual.min).isEqualTo(stat.min)
        Assertions.assertThat(actual.sum).isCloseTo(stat.sum, offset)
        return this
    }
}