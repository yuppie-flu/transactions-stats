package com.github.yuppieflu.stats.service.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Statistic implements Stat {
    private final double sum;
    private final double avg;
    private final double min;
    private final double max;
    private final long count;
}
