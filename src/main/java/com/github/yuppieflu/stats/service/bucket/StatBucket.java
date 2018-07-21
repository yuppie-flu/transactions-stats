package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.domain.Measurement;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RequiredArgsConstructor
class StatBucket {
    private final int index;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private long count;
    private double max;
    private double min;
    private double sum;
    private long lastAddedTimestamp;

    void addMeasurement(Measurement m) {
        readWriteLock.writeLock().lock();
        try {
            if (lastAddedTimestamp - m.getTimestamp() > BucketsStorageService.ONE_BUCKET_MILLIS) {
                return;
            }
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
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    boolean shouldSkip(long timestamp) {
        readWriteLock.readLock().lock();
        try {
            if (count == 0) {
                return true;
            }
            int currentIndex = TimeUtils.secondOfMinute(timestamp);
            if (currentIndex == index) {
                return timestamp - lastAddedTimestamp > BucketsStorageService.ONE_BUCKET_MILLIS;
            }
            return timestamp - lastAddedTimestamp > BucketsStorageService.BUCKETS_SIZE_MILLIS;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    StatBucketContent content() {
        readWriteLock.readLock().lock();
        try {
            return StatBucketContent.builder()
                                    .count(this.count)
                                    .max(this.max)
                                    .min(this.min)
                                    .sum(this.sum)
                                    .build();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
