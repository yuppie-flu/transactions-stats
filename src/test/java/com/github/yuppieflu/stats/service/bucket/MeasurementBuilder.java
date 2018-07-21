package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.domain.Measurement;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

class MeasurementBuilder {

    private static final Instant FIXED_TS = Instant.now();
    private static final double MAX_MEASUREMENT_VALUE = 10.0;
    private static final long ONE_SEC_MILLIS = 1000;
    private static final int DEF_SECONDS_AGO = 5;

    static Measurement getRandMeasurementFromOneBucket() {
        return getRandNotOldMeasurementFromBucket(FIXED_TS, DEF_SECONDS_AGO);
    }

    static Measurement getRandNotOldMeasurementFromBucket(Instant instant, int secondsAgo) {
        final Instant startSecond = instant.truncatedTo(ChronoUnit.SECONDS);
        long timestamp = startSecond.minusSeconds(secondsAgo).toEpochMilli() +
                ThreadLocalRandom.current().nextLong(ONE_SEC_MILLIS);
        return new Measurement(timestamp, ThreadLocalRandom.current().nextDouble(MAX_MEASUREMENT_VALUE));
    }
}
