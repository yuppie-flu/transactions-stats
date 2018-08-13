package com.github.yuppieflu.stats.service.bucket

import com.github.yuppieflu.stats.service.domain.Measurement
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.math.max
import kotlin.math.min

class StatBucket {
    private val readWriteLock = ReentrantReadWriteLock(true)
    private var count = 0L
    private var min = 0.0
    private var max = 0.0
    private var sum = 0.0
    private var lastAddedTimestamp = 0L

    fun addMeasurement(m: Measurement) {
        readWriteLock.writeLock().lock()
        try {
            if (lastAddedTimestamp < m.timestamp) {
                replaceOldData(m)
            } else {
                add(m)
            }
            lastAddedTimestamp = m.timestamp
        } finally {
            readWriteLock.writeLock().unlock()
        }
    }

    private fun replaceOldData(m: Measurement) {
        count = 1L
        min = m.value
        max = m.value
        sum = m.value
    }

    private fun add(m: Measurement) {
        count++
        min = min(min, m.value)
        max = max(max, m.value)
        sum += m.value
    }

    fun isWithin60SecondsFrom(timestamp: Long): Boolean {
        readWriteLock.readLock().lock()
        try {
            return timestamp - this.lastAddedTimestamp <= BucketsStorageService.SIZE
        } finally {
            readWriteLock.readLock().unlock()
        }
    }

    fun content(): StatBucketContent {
        readWriteLock.readLock().lock()
        try {
            return StatBucketContent(count = count, min = min, max = max, sum = sum)
        } finally {
            readWriteLock.readLock().unlock()
        }
    }
}