package com.github.yuppieflu.stats.service;

import com.github.yuppieflu.stats.service.domain.Measurement;
import com.github.yuppieflu.stats.service.domain.Statistic;
import com.github.yuppieflu.stats.service.domain.Status;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class RingBufferStorageService implements StorageService{

    private static final long WINDOW_SIZE_MILLIS = TimeUnit.SECONDS.toMillis(60);

    private final Clock clock;

    @Override
    public Status addMeasurement(Measurement m) {
        if (m.getTimestamp() < clock.millis() - WINDOW_SIZE_MILLIS) {
            return Status.REJECTED;
        }
        return Status.PROCESSED;
    }

    @Override
    public Statistic getStatistic() {
        return Statistic.builder()
                        .sum(0.0)
                        .avg(0.0)
                        .max(0.0)
                        .min(0.0)
                        .count(0L)
                        .build();
    }
}
