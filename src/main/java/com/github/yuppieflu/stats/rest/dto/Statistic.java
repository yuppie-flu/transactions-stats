package com.github.yuppieflu.stats.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Statistic {
    private final double sum;
    private final double avg;
    private final double max;
    private final double min;
    private final long count;

    public static Statistic from(com.github.yuppieflu.stats.service.domain.Statistic statistic) {
        return Statistic.builder()
                        .count(statistic.getCount())
                        .max(statistic.getMax())
                        .min(statistic.getMin())
                        .avg(statistic.getAvg())
                        .sum(statistic.getSum())
                        .build();
    }
}
