package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.StorageService;
import com.github.yuppieflu.stats.service.domain.Measurement;
import com.github.yuppieflu.stats.service.domain.Statistic;
import com.github.yuppieflu.stats.service.domain.Status;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.DoubleSummaryStatistics;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class BucketsStorageServiceTest {

    private static final Instant FIXED_TS = Instant.now();
    private static final Instant FIXED_TS_SECONDS = FIXED_TS.truncatedTo(ChronoUnit.SECONDS);

    private StorageService storageService = new BucketsStorageService(Clock.fixed(FIXED_TS, ZoneOffset.UTC));

    @Test
    public void rejectedStatusForTooOldTransaction() {
        // setup
        Measurement m = new Measurement(FIXED_TS.minusSeconds(60).toEpochMilli() - 1, 1.0);

        // when
        Status status = storageService.addMeasurement(m);

        // then
        assertThat(status).isEqualTo(Status.REJECTED);
    }

    @Test
    public void tooOldMeasurementsAreIgnored() {
        // setup
        Measurement m = new Measurement(FIXED_TS.minusSeconds(60).toEpochMilli() - 1, 1.0);
        storageService.addMeasurement(m);

        // when
        Statistic statistic = storageService.getStatistic();

        // then
        assertThat(statistic.getCount()).isEqualTo(0);
        assertThat(statistic.getMax()).isEqualTo(0.0);
        assertThat(statistic.getMin()).isEqualTo(0.0);
        assertThat(statistic.getSum()).isEqualTo(0.0);
        assertThat(statistic.getAvg()).isEqualTo(0.0);
    }

    @Test
    public void processedStatusForTransactionWithinMinute() {
        // setup
        Measurement m = new Measurement(FIXED_TS.minusSeconds(60).toEpochMilli(), 1.0);

        // when
        Status status = storageService.addMeasurement(m);

        // then
        assertThat(status).isEqualTo(Status.PROCESSED);
    }

    @Test
    public void aggregationWithinOneBucketWorks() {
        // setup
        DoubleSummaryStatistics expectedStats = Stream.generate(this::getRandMeasurement)
                                                      .limit(3)
                                                      .peek(storageService::addMeasurement)
                                                      .collect(Collectors.summarizingDouble(Measurement::getValue));

        // when
        Statistic statistic = storageService.getStatistic();

        // then
        assertThat(statistic.getCount()).isEqualTo(expectedStats.getCount());
        assertThat(statistic.getMax()).isEqualTo(expectedStats.getMax());
        assertThat(statistic.getMin()).isEqualTo(expectedStats.getMin());
        assertThat(statistic.getSum()).isEqualTo(expectedStats.getSum());
        assertThat(statistic.getAvg()).isEqualTo(expectedStats.getAverage());
    }

    @Test
    public void aggregationWithinSeveralBucketsWorks() {
        // setup
        DoubleSummaryStatistics expectedStats = IntStream.of(2, 33, 17, 2, 33, 2)
                                                         .mapToObj(this::getRandMeasurementSecondsAgo)
                                                         .peek(storageService::addMeasurement)
                                                         .collect(Collectors.summarizingDouble(Measurement::getValue));

        // when
        Statistic statistic = storageService.getStatistic();

        // then
        assertThat(statistic.getCount()).isEqualTo(expectedStats.getCount());
        assertThat(statistic.getMax()).isEqualTo(expectedStats.getMax());
        assertThat(statistic.getMin()).isEqualTo(expectedStats.getMin());
        assertThat(statistic.getSum()).isEqualTo(expectedStats.getSum());
        assertThat(statistic.getAvg()).isEqualTo(expectedStats.getAverage());
    }

    private Measurement getRandMeasurement() {
        return getRandMeasurementSecondsAgo(5);
    }

    private Measurement getRandMeasurementSecondsAgo(int seconds) {
        long timestamp = FIXED_TS_SECONDS.minusSeconds(seconds).toEpochMilli() + ThreadLocalRandom.current().nextLong(1000);
        return new Measurement(timestamp, ThreadLocalRandom.current().nextDouble(10.0));
    }
}
