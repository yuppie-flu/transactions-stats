package com.github.yuppieflu.stats.service.bucket;

import lombok.AllArgsConstructor;
import lombok.Setter;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@AllArgsConstructor
@Setter
class ManagedClock extends Clock {

    private Clock clock;

    @Override
    public ZoneId getZone() {
        return clock.getZone();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return clock.withZone(zone);
    }

    @Override
    public Instant instant() {
        return clock.instant();
    }
}
