package com.github.yuppieflu.stats.service.domain

import com.github.yuppieflu.stats.rest.dto.Transaction

class Measurement(val timestamp: Long, val value: Double) {
    constructor(transaction: Transaction): this(transaction.timestamp, transaction.amount)
}