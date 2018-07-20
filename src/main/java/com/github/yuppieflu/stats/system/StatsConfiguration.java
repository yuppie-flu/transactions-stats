package com.github.yuppieflu.stats.system;

import com.github.yuppieflu.stats.service.RingBufferStorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class StatsConfiguration {

    @Bean
    public RingBufferStorageService storageService() {
        return new RingBufferStorageService(Clock.systemUTC());
    }
}
