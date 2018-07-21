package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.domain.Statistic;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode
class StatBucketContent {

    public static final StatBucketContent EMPTY =
            StatBucketContent.builder().count(0).max(0.0).min(0.0).sum(0.0).build();

    private final long count;
    private final double max;
    private final double min;
    private final double sum;

    static StatBucketContent add(StatBucketContent a, StatBucketContent b) {
        return StatBucketContent.builder()
                                .count(a.count + b.count)
                                .max(Math.max(a.max, b.max))
                                .min(Math.min(a.min, b.min))
                                .sum(a.sum + b.sum)
                                .build();
    }

    Statistic toStatistic() {
        return Statistic.builder()
                        .count(this.count)
                        .max(this.max)
                        .min(this.min)
                        .sum(this.sum)
                        .avg(this.count == 0 ? 0.0 : this.sum / this.count)
                        .build();
    }
}
