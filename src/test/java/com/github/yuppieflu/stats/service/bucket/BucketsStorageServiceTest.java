package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.StorageService;
import com.github.yuppieflu.stats.service.domain.Measurement;
import com.github.yuppieflu.stats.service.domain.Statistic;
import com.github.yuppieflu.stats.service.domain.Status;
import org.junit.Before;
import org.junit.Test;

import java.time.*;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.yuppieflu.stats.service.bucket.BucketsStorageServiceTestUtils.empty;
import static org.assertj.core.api.Assertions.assertThat;

public class BucketsStorageServiceTest {

    private static final Instant FIXED_TS = Instant.now();

    private StorageService storageService;

    @Before
    public void setup() {
        storageService = new BucketsStorageService(Clock.fixed(FIXED_TS, ZoneOffset.UTC));
    }

    @Test
    public void invalidStatusForFutureTransaction() {
        // setup
        Measurement m = new Measurement(FIXED_TS.plusMillis(1).toEpochMilli(), 1.0);

        // when
        Status status = storageService.addMeasurement(m);

        // then
        assertThat(status).isEqualTo(Status.INVALID);
    }

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
        assertThat(statistic).isEqualTo(empty());
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
        assertThat(statistic).isEqualTo(from(expectedStats));
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
        assertThat(statistic).isEqualTo(from(expectedStats));
    }

    private Measurement getRandMeasurement() {
        return getRandMeasurementSecondsAgo(5);
    }

    private Measurement getRandMeasurementSecondsAgo(int secondsAgo) {
        return BucketsStorageServiceTestUtils.getRandNotOldMeasurementFromBucket(FIXED_TS, secondsAgo);
    }

    private static Statistic from(DoubleSummaryStatistics s) {
        return Statistic.builder()
                        .count(s.getCount())
                        .max(s.getMax())
                        .min(s.getMin())
                        .sum(s.getSum())
                        .avg(s.getAverage())
                        .build();
    }
}
