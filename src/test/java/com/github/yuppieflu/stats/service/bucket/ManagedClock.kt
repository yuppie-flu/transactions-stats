package com.github.yuppieflu.stats.service.bucket

import java.time.Clock
import java.time.ZoneId

class ManagedClock(var clock: Clock) : Clock() {
    override fun withZone(zone: ZoneId?) = clock.withZone(zone)

    override fun getZone() = clock.zone

    override fun instant() = clock.instant()
}
