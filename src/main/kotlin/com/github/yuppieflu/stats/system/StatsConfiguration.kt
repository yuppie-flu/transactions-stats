package com.github.yuppieflu.stats.system

import com.github.yuppieflu.stats.service.StorageService
import com.github.yuppieflu.stats.service.bucket.BucketsStorageService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import java.time.Clock

@Configuration
open class StatsConfiguration {

    @Bean
    open fun storageService(): StorageService {
        return BucketsStorageService(Clock.systemUTC())
    }
}
