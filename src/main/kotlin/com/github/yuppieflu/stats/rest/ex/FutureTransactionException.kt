package com.github.yuppieflu.stats.rest.ex

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Transaction timestamp is in the future")
class FutureTransactionException: RuntimeException()