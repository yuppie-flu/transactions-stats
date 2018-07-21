package com.github.yuppieflu.stats.service.bucket;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeUtilsTest {

    @Test
    public void testSecondsOfMinute() {
        // setup
        long epochMillis = LocalDateTime.parse("2007-12-03T10:15:49")
                                        .toInstant(ZoneOffset.UTC)
                                        .plusMillis(587)
                                        .toEpochMilli();

        // expect
        assertThat(TimeUtils.secondOfMinute(epochMillis)).isEqualTo(49);
    }
}
