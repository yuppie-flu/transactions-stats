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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BucketsStorageService implements StorageService {

    private static final int BUCKETS_SIZE_SEC = 60;
    static final long BUCKETS_SIZE_MILLIS = TimeUnit.SECONDS.toMillis(BUCKETS_SIZE_SEC);
    static final long ONE_BUCKET_MILLIS = TimeUnit.SECONDS.toMillis(1);

    private final Clock clock;
    private final List<StatBucket> buckets;

    public BucketsStorageService(Clock clock) {
        this.clock = clock;
        this.buckets = Stream.generate(StatBucket::new)
                             .limit(ONE_BUCKET_MILLIS)
                             .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Status addMeasurement(Measurement m) {
        long measurementTs = m.getTimestamp();
        long millisInPast = clock.millis() - measurementTs;
        if (millisInPast > BUCKETS_SIZE_MILLIS) {
            return Status.REJECTED;
        }
        int bucketIndex = LocalDateTime.ofEpochSecond(measurementTs / 1000, (int)(measurementTs % 1000) * 1000, ZoneOffset.UTC)
                                       .get(ChronoField.SECOND_OF_MINUTE);
        buckets.get(bucketIndex).addMeasurement(m);
        return Status.PROCESSED;
    }

    @Override
    public Statistic getStatistic() {
        final long requestTimestamp = clock.millis();
        return buckets.stream()
                      .filter(bucket -> !bucket.shouldSkip(requestTimestamp))
                      .map(StatBucket::toAccumulator)
                      .reduce(StatBucketAccumulator::add)
                      .orElse(StatBucketAccumulator.EMPTY)
                      .toStatistic();
    }
}
