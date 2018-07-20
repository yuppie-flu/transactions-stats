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
}
