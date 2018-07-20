package com.github.yuppieflu.stats.service.domain;

import com.github.yuppieflu.stats.rest.dto.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Measurement {
    private final long timestamp;
    private final double value;

    public Measurement(Transaction transaction) {
        this.timestamp = transaction.getTimestamp();
        this.value = transaction.getAmount();
    }
}
