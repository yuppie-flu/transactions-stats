package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.domain.Measurement;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RequiredArgsConstructor
class StatBucket {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private long count;
    private double max;
    private double min;
    private double sum;
    private long lastAddedTimestamp;

    void addMeasurement(Measurement m) {
        readWriteLock.writeLock().lock();
        try {
            if (lastAddedTimestamp < m.getTimestamp()) {
                replaceOldData(m);
            } else {
                add(m);
            }
            lastAddedTimestamp = m.getTimestamp();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void replaceOldData(Measurement m) {
        count = 1;
        max = m.getValue();
        min = m.getValue();
        sum = m.getValue();
    }

    private void add(Measurement m) {
        count++;
        max = Math.max(max, m.getValue());
        min = Math.min(min, m.getValue());
        sum += m.getValue();
    }

    boolean isWithin60SecondsFrom(long timestamp) {
        readWriteLock.readLock().lock();
        try {
            return timestamp - this.lastAddedTimestamp <= BucketsStorageService.SIZE;
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
