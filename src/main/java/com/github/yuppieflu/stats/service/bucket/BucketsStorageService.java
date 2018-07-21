package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.StorageService;
import com.github.yuppieflu.stats.service.domain.Measurement;
import com.github.yuppieflu.stats.service.domain.Statistic;
import com.github.yuppieflu.stats.service.domain.Status;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BucketsStorageService implements StorageService {

    private static final int BUCKETS_SIZE_SEC = 60;
    static final long BUCKETS_SIZE_MILLIS = TimeUnit.SECONDS.toMillis(BUCKETS_SIZE_SEC);
    static final long ONE_BUCKET_MILLIS = TimeUnit.SECONDS.toMillis(1);

    private final Clock clock;
    private final List<StatBucket> buckets;

    public BucketsStorageService(Clock clock) {
        this.clock = clock;
        this.buckets = IntStream.range(0, 60)
                                .mapToObj(StatBucket::new)
                                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Status addMeasurement(Measurement m) {
        long measurementTs = m.getTimestamp();
        long millisInPast = clock.millis() - measurementTs;
        if (millisInPast < 0) {
            return Status.INVALID;
        }
        if (millisInPast > BUCKETS_SIZE_MILLIS) {
            return Status.REJECTED;
        }
        int bucketIndex = TimeUtils.secondOfMinute(measurementTs);
        buckets.get(bucketIndex).addMeasurement(m);
        return Status.PROCESSED;
    }

    @Override
    public Statistic getStatistic() {
        final long requestTimestamp = clock.millis();
        return buckets.stream()
                      .filter(bucket -> !bucket.shouldSkip(requestTimestamp))
                      .map(StatBucket::content)
                      .reduce(StatBucketContent::add)
                      .orElse(StatBucketContent.EMPTY)
                      .toStatistic();
    }
}
