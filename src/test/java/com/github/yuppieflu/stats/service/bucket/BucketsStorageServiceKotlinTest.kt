package com.github.yuppieflu.stats.service.bucket

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

class BucketsStorageServiceKotlinTest {

    private val storageService = BucketsStorageService(Clock.systemUTC())

    @Test
    fun testMillSecondsOfMinute() {
        // setup
        val epochMillis = LocalDateTime.parse("2007-12-03T10:15:49")
                .toInstant(ZoneOffset.UTC)
                .plusMillis(587)
                .toEpochMilli()

        // expect
        assertThat(storageService.milliSecondOfMinute(epochMillis)).isEqualTo(49587)
    }
}