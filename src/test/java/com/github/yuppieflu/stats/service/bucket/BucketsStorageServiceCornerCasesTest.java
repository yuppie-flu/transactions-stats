package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.StorageService;
import com.github.yuppieflu.stats.service.domain.Measurement;
import com.github.yuppieflu.stats.service.domain.Statistic;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class BucketsStorageServiceCornerCasesTest {

    private static final Instant HOUR_BEGINNING = LocalDateTime.now().toInstant(ZoneOffset.UTC)
                                                               .truncatedTo(ChronoUnit.HOURS);


    private ManagedClock clock = new ManagedClock(Clock.systemUTC());
    private StorageService storageService;


    @Before
    public void setup() {
        storageService = new BucketsStorageService(clock);
    }

    @Test
    public void oldValuesInBucketOfRequestTimestampAreIgnored() {
        // setup
        // e.g. 16:01:00,000
        Instant timeNow = HOUR_BEGINNING.plus(1, ChronoUnit.MINUTES).plusMillis(1);
        // e.g. 16:00:00,999 - so in this case this measurement is ignored
        Instant timeMeasurement = HOUR_BEGINNING.plusMillis(999);

        moveClock(timeNow);
        storageService.addMeasurement(new Measurement(timeMeasurement.toEpochMilli(), 1.0));

        // when
        Statistic statistic = storageService.getStatistic();

        // then
        assertThat(statistic.getCount()).isEqualTo(0);
    }

    @Test
    public void valuesNotOlderThan60SecondsFromOtherBuckets() {
        // setup
        // e.g. 16:01:00,999
        Instant timeNow = HOUR_BEGINNING.plus(1, ChronoUnit.MINUTES).plusMillis(999);
        // e.g. 16:00:01,000 - so in this case this measurement is used for statistic
        Instant timeMeasurement = HOUR_BEGINNING.plus(1, ChronoUnit.SECONDS);

        moveClock(timeNow);
        storageService.addMeasurement(new Measurement(timeMeasurement.toEpochMilli(), 1.0));

        // when
        Statistic statistic = storageService.getStatistic();

        // then
        assertThat(statistic.getCount()).isEqualTo(1);
    }


    @Test
    public void valuesOlderThan60SecondsIgnored() {
        // setup
        // e.g. 16:01:00,000
        Instant timeNow = HOUR_BEGINNING.plus(1, ChronoUnit.MINUTES);
        // e.g. 15:59:59,999 - so in this case this measurement ignored
        Instant timeMeasurement = HOUR_BEGINNING.minusMillis(1);

        moveClock(HOUR_BEGINNING.plusSeconds(30));
        storageService.addMeasurement(new Measurement(timeMeasurement.toEpochMilli(), 1.0));
        moveClock(timeNow);

        // when
        Statistic statistic = storageService.getStatistic();

        // then
        assertThat(statistic.getCount()).isEqualTo(0);
    }

    @Test
    public void newValuesDropPreviousMinuteStat() {
        // setup
        // e.g. 16:01:00,000
        Instant timeNow = HOUR_BEGINNING.plus(1, ChronoUnit.MINUTES);
        // e.g. 15:59:59,999 - so in this case this measurement will be dropped by the next one
        Instant timeOldMeasurement = HOUR_BEGINNING.minusMillis(1);
        // e.g. 16:00:59,999 - next measurement
        Instant timeNewMeasurement = timeNow.minusMillis(1);

        moveClock(HOUR_BEGINNING.plusSeconds(30));
        storageService.addMeasurement(new Measurement(timeOldMeasurement.toEpochMilli(), 1.0));
        moveClock(HOUR_BEGINNING);
        storageService.addMeasurement(new Measurement(timeNewMeasurement.toEpochMilli(), 2.0));
        // move clock
        clock.setClock(Clock.fixed(timeNow, ZoneOffset.UTC));

        // when
        Statistic statistic = storageService.getStatistic();

        // then
        assertThat(statistic.getCount()).isEqualTo(1);
        assertThat(statistic.getSum()).isEqualTo(2.0);
    }

    @Test
    public void oldValuesFromTheSameBucketNotSaved() {
        // setup
        // e.g. 16:01:00,999
        Instant timeNow = HOUR_BEGINNING.plus(1, ChronoUnit.MINUTES).plusMillis(999);
        // e.g. 16:01:000,250 - next measurement
        Instant timeNewMeasurement = HOUR_BEGINNING.plus(1, ChronoUnit.MINUTES).plusMillis(250);
        // e.g. 16:00:000,500 - so in this case this measurement will not be saved
        Instant timeOldMeasurement = HOUR_BEGINNING.minusMillis(500);

        moveClock(timeNow);
        storageService.addMeasurement(new Measurement(timeNewMeasurement.toEpochMilli(), 2.0));
        storageService.addMeasurement(new Measurement(timeOldMeasurement.toEpochMilli(), 1.0));

        // when
        Statistic statistic = storageService.getStatistic();

        // then
        assertThat(statistic.getCount()).isEqualTo(1);
        assertThat(statistic.getSum()).isEqualTo(2.0);
    }

    private void moveClock(Instant instant) {
        clock.setClock(Clock.fixed(instant, ZoneOffset.UTC));
    }
}
