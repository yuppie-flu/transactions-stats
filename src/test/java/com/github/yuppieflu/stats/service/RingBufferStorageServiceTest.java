package com.github.yuppieflu.stats.service;

import com.github.yuppieflu.stats.service.domain.Measurement;
import com.github.yuppieflu.stats.service.domain.Status;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class RingBufferStorageServiceTest {

    private static final Instant FIXED_TS = Instant.now();

    @Test
    public void rejectedStatusForTooOldTransaction() {
        // setup
        StorageService storageService = new RingBufferStorageService(Clock.fixed(FIXED_TS, ZoneOffset.UTC));
        Measurement m = new Measurement(FIXED_TS.minus(60, ChronoUnit.SECONDS).toEpochMilli() - 1, 1.0);

        // when
        Status status = storageService.addMeasurement(m);

        // then
        assertThat(status).isEqualTo(Status.REJECTED);
    }

    @Test
    public void processedStatusForTransactionWithinMinute() {
        // setup
        StorageService storageService = new RingBufferStorageService(Clock.fixed(FIXED_TS, ZoneOffset.UTC));
        Measurement m = new Measurement(FIXED_TS.minus(60, ChronoUnit.SECONDS).toEpochMilli(), 1.0);

        // when
        Status status = storageService.addMeasurement(m);

        // then
        assertThat(status).isEqualTo(Status.PROCESSED);
    }
}
