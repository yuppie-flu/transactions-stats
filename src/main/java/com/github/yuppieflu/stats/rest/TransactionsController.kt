package com.github.yuppieflu.stats.rest

import com.github.yuppieflu.stats.rest.dto.Statistic
import com.github.yuppieflu.stats.rest.dto.Transaction
import com.github.yuppieflu.stats.rest.ex.FutureTransactionException
import com.github.yuppieflu.stats.service.StorageService
import com.github.yuppieflu.stats.service.domain.Measurement
import com.github.yuppieflu.stats.service.domain.Status
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionsController(private val storageService: StorageService) {

    @PostMapping("/transactions")
    fun transactions(@RequestBody transaction: Transaction): ResponseEntity<Void> {
        val status = storageService.addMeasurement(Measurement(transaction))
        return when (status) {
            Status.REJECTED -> ResponseEntity.noContent().build()
            Status.PROCESSED -> ResponseEntity.status(HttpStatus.CREATED).build()
            Status.INVALID -> throw FutureTransactionException()
        }
    }

    @GetMapping("/statistics")
    fun statistics(): Statistic {
        return Statistic(storageService.getStatistic())
    }
}