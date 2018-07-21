package com.github.yuppieflu.stats.util;

import com.github.yuppieflu.stats.service.domain.Stat;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;

import java.util.DoubleSummaryStatistics;

public class StatAssert extends AbstractAssert<StatAssert, Stat> {

    public StatAssert(Stat stat) {
        super(stat, StatAssert.class);
    }

    public static StatAssert assertThat(Stat stat) {
        return new StatAssert(stat);
    }

    public StatAssert isCloseTo(DoubleSummaryStatistics summaryStatistics, Offset<Double> offset) {
        Assertions.assertThat(actual.getCount()).isEqualTo(summaryStatistics.getCount());
        Assertions.assertThat(actual.getMax()).isEqualTo(summaryStatistics.getMax());
        Assertions.assertThat(actual.getMin()).isEqualTo(summaryStatistics.getMin());
        Assertions.assertThat(actual.getSum()).isCloseTo(summaryStatistics.getSum(), offset);
        Assertions.assertThat(actual.getAvg()).isCloseTo(summaryStatistics.getAverage(), offset);
        return this;
    }
}
