package com.github.yuppieflu.stats.rest.ex

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
class InternalErrorException : RuntimeException()
