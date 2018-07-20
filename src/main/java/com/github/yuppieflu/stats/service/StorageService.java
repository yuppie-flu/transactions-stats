package com.github.yuppieflu.stats.service;

import com.github.yuppieflu.stats.service.domain.Measurement;
import com.github.yuppieflu.stats.service.domain.Statistic;
import com.github.yuppieflu.stats.service.domain.Status;

public interface StorageService {
    Status addMeasurement(Measurement measurement);
    Statistic getStatistic();
}
