package com.github.yuppieflu.stats.rest;

import com.github.yuppieflu.stats.rest.dto.Statistic;
import com.github.yuppieflu.stats.rest.dto.Transaction;
import com.github.yuppieflu.stats.rest.ex.FutureTransactionException;
import com.github.yuppieflu.stats.rest.ex.InternalErrorException;
import com.github.yuppieflu.stats.service.StorageService;
import com.github.yuppieflu.stats.service.domain.Measurement;
import com.github.yuppieflu.stats.service.domain.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionsController {

    private final StorageService storageService;

    @PostMapping("/transactions")
    public ResponseEntity<Void> transactions(@RequestBody Transaction transaction) {
        Status status = storageService.addMeasurement(new Measurement(transaction));
        switch (status) {
            case REJECTED: return ResponseEntity.noContent().build();
            case PROCESSED: return ResponseEntity.status(HttpStatus.CREATED).build();
            case INVALID: throw new FutureTransactionException();
            default:
                log.error("Unknown status [{}]", status);
                throw new InternalErrorException();
        }
    }

    @GetMapping("/statistics")
    public Statistic statistics() {
        return Statistic.from(storageService.getStatistic());
    }
}
