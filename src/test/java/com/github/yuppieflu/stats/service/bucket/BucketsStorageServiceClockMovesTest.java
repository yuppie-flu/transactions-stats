package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.StorageService;
import com.github.yuppieflu.stats.service.domain.Measurement;
import com.github.yuppieflu.stats.service.domain.Statistic;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

public class BucketsStorageServiceClockMovesTest {

    private ManagedClock clock = new ManagedClock(Clock.systemUTC());
    private StorageService storageService;

    @Before
    public void setup() {
        storageService = new BucketsStorageService(clock);
    }

    @Test
    public void valuesMoreThat60SecOldAreNotIncludedInStat() {
        Instant now = Instant.now();
        Instant mTime = now.minusSeconds(60).minusMillis(1);

        moveClock(now.minusSeconds(30));
        storageService.addMeasurement(measurement(mTime, 1.0));
        moveClock(now);

        // when
        Statistic statistic = storageService.getStatistic();

        // then
        assertThat(statistic.getCount()).isEqualTo(0);
    }

    @Test
    public void values60SecOldAreIncludedInStat() {
        Instant now = Instant.now();
        Instant mTime= now.minusSeconds(60);

        moveClock(now.minusSeconds(30));
        storageService.addMeasurement(measurement(mTime, 1.0));
        moveClock(now);

        // when
        Statistic statistic = storageService.getStatistic();

        // then
        assertThat(statistic.getCount()).isEqualTo(1);
    }

    @Test
    public void newValuesOverrideOldValuesFromTheSameBucket() {
        Instant now = Instant.now();
        Instant oldMTime = now.minusSeconds(60);
        double oldValue = 1.0;
        double newValue = 2.0;

        moveClock(now.minusSeconds(30));
        storageService.addMeasurement(measurement(oldMTime, oldValue));
        moveClock(now);

        storageService.addMeasurement(measurement(now, newValue));

        // when
        Statistic statistic = storageService.getStatistic();

        // then
        assertThat(statistic.getCount()).isEqualTo(1);
        assertThat(statistic.getSum()).isEqualTo(newValue);
    }

    private void moveClock(Instant instant) {
        clock.setClock(Clock.fixed(instant, ZoneOffset.UTC));
    }

    private static Measurement measurement(Instant instant, double value) {
        return new Measurement(instant.toEpochMilli(), value);
    }
}
