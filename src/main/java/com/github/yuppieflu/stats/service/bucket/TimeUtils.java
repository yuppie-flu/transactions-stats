package com.github.yuppieflu.stats.service.bucket;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

class TimeUtils {

    static int secondOfMinute(long epochMillis) {
        return LocalDateTime.ofEpochSecond(epochMillis / 1000, 0, ZoneOffset.UTC)
                            .get(ChronoField.SECOND_OF_MINUTE);
    }
}
