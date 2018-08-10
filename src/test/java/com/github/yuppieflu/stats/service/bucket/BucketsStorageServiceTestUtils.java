package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.domain.Measurement;
import com.github.yuppieflu.stats.service.domain.Statistic;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

class BucketsStorageServiceTestUtils {

    private static final Instant FIXED_TS = Instant.now();
    private static final double MAX_MEASUREMENT_VALUE = 10.0;
    private static final int DEF_SECONDS_AGO = 5;

    static Measurement getRandMeasurementFromOneBucket() {
        return getRandNotOldMeasurementFromBucket(FIXED_TS, DEF_SECONDS_AGO);
    }

    static Measurement getRandNotOldMeasurementFromBucket(Instant instant, int secondsAgo) {
        long timestamp = instant.minusSeconds(secondsAgo).toEpochMilli();
        return new Measurement(timestamp, ThreadLocalRandom.current().nextDouble(MAX_MEASUREMENT_VALUE));
    }

    static Statistic empty() {
        return new Statistic(0, 0.0, 0.0, 0.0);
    }
}
