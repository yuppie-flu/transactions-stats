package com.github.yuppieflu.stats.service.bucket;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

class TimeUtils {

    static int secondOfMinute(long epochMillis) {
        long seconds = epochMillis / 1000;
        int nanos = (int)(epochMillis % 1000) * 1000;
        return LocalDateTime.ofEpochSecond(seconds, nanos, ZoneOffset.UTC).get(ChronoField.SECOND_OF_MINUTE);
    }
}
