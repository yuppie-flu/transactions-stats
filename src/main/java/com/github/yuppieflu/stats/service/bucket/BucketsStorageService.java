package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.StorageService;
import com.github.yuppieflu.stats.service.domain.Measurement;
import com.github.yuppieflu.stats.service.domain.Statistic;
import com.github.yuppieflu.stats.service.domain.Status;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BucketsStorageService implements StorageService {

    static final long SIZE = 60_000;

    private final Clock clock;
    private final List<StatBucket> buckets;

    public BucketsStorageService(Clock clock) {
        this.clock = clock;
        this.buckets = Stream.generate(StatBucket::new)
                             .limit(SIZE)
                             .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Status addMeasurement(Measurement m) {
        long measurementTs = m.getTimestamp();
        long millisInPast = clock.millis() - measurementTs;
        if (millisInPast < 0) {
            return Status.INVALID;
        }
        if (millisInPast > SIZE) {
            return Status.REJECTED;
        }
        int bucketIndex = milliSecondOfMinute(measurementTs);
        buckets.get(bucketIndex).addMeasurement(m);
        return Status.PROCESSED;
    }

    static int milliSecondOfMinute(long epochMillis) {
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(
                epochMillis / 1000, 0, ZoneOffset.UTC);
        int millis = (int)(epochMillis % 1000);
        return localDateTime.get(ChronoField.SECOND_OF_MINUTE) * 1000 + millis;
    }

    @Override
    public Statistic getStatistic() {
        final long requestTimestamp = clock.millis();
        return buckets.stream()
                      .filter(b -> b.isWithin60SecondsFrom(requestTimestamp))
                      .map(StatBucket::content)
                      .reduce(StatBucketContent.Companion::add)
                      .orElse(StatBucketContent.Companion.getEMPTY())
                      .toStatistic();
    }
}
