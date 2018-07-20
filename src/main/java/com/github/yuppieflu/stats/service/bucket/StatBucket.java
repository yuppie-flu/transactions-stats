package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.domain.Measurement;

class StatBucket {
    private long count;
    private double max;
    private double min;
    private double sum;
    private long lastAddedTimestamp;

    synchronized void addMeasurement(Measurement m) {
        if (m.getTimestamp() - lastAddedTimestamp > BucketsStorageService.ONE_BUCKET_MILLIS) {
            count = 1;
            max = m.getValue();
            min = m.getValue();
            sum = m.getValue();
        } else {
            count++;
            max = Math.max(max, m.getValue());
            min = Math.min(min, m.getValue());
            sum += m.getValue();
        }
        lastAddedTimestamp = m.getTimestamp();

    }

    synchronized boolean shouldSkip(long timestamp) {
        return count == 0 || timestamp - lastAddedTimestamp > BucketsStorageService.BUCKETS_SIZE_MILLIS;
    }

    synchronized StatBucketAccumulator toAccumulator() {
        return StatBucketAccumulator.builder()
                                    .count(this.count)
                                    .max(this.max)
                                    .min(this.min)
                                    .sum(this.sum)
                                    .build();
    }
}
