package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.domain.Statistic;
import lombok.Builder;

@Builder
class StatBucketAccumulator {

    public static final StatBucketAccumulator EMPTY =
            StatBucketAccumulator.builder().count(0).max(0.0).min(0.0).sum(0.0).build();

    private final long count;
    private final double max;
    private final double min;
    private final double sum;

    static StatBucketAccumulator add(StatBucketAccumulator a, StatBucketAccumulator b) {
        return StatBucketAccumulator.builder()
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
                        .avg(this.sum / this.count)
                        .build();
    }
}
