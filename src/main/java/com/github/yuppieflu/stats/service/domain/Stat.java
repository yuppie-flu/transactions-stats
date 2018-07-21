package com.github.yuppieflu.stats.service.domain;

public interface Stat {
    long getCount();
    double getMin();
    double getMax();
    double getSum();
    double getAvg();
}
