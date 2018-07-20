package com.github.yuppieflu.stats.system;

import com.github.yuppieflu.stats.service.bucket.BucketsStorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class StatsConfiguration {

    @Bean
    public BucketsStorageService storageService() {
        return new BucketsStorageService(Clock.systemUTC());
    }
}
