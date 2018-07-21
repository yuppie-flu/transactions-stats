package com.github.yuppieflu.stats.service.bucket;

import com.github.yuppieflu.stats.service.domain.Measurement;
import com.github.yuppieflu.stats.service.domain.Statistic;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.summarizingDouble;
import static org.assertj.core.api.Assertions.assertThat;

public class StatBucketTest {

    private StatBucket bucket;

    @Before
    public void setup() {
        bucket = new StatBucket();
    }

    @Test
    public void testConcurrentAddMeasurements() {
        // setup
        List<Measurement> measurements = Stream.generate(MeasurementBuilder::getRandMeasurementFromOneBucket)
                                               .limit(10000)
                                               .collect(Collectors.toList());
        DoubleSummaryStatistics expectedStats = measurements.stream()
                                                            .collect(summarizingDouble(Measurement::getValue));
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<CompletableFuture<Void>> results =
                measurements.stream()
                            .map(m -> CompletableFuture.runAsync(() -> addMeasurementWithLatch(m, latch), executor))
                            .collect(Collectors.toList());
        latch.countDown();
        results.forEach(CompletableFuture::join);

        // when
        Statistic statistic = bucket.toAccumulator().toStatistic();

        // then
        assertThat(statistic.getCount()).isEqualTo(expectedStats.getCount());
    }

    private void addMeasurementWithLatch(Measurement m, CountDownLatch latch) {
        try {
            latch.await();
            bucket.addMeasurement(m);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
