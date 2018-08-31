package com.github.yuppieflu.stats

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class StatsApplication {

    fun main(args: Array<String>) {
        SpringApplication.run(StatsApplication::class.java, *args)
    }
}
