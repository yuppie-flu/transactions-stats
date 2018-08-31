package com.github.yuppieflu.stats.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.yuppieflu.stats.rest.dto.Transaction
import com.github.yuppieflu.stats.service.StorageService
import com.github.yuppieflu.stats.service.domain.Statistic
import com.github.yuppieflu.stats.service.domain.Status
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@WebMvcTest(TransactionsController::class)
class TransactionsControllerTest {

    companion object {
        val TRANSACTION = Transaction(System.currentTimeMillis(), 1.0)
        val EMPTY_BODY = ByteArray(0)
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var storageService: StorageService

    val mapper = ObjectMapper()

    @Test
    fun badRequestStatusForFutureTransactions() {
        // setup
        val body = mapper.writeValueAsString(TRANSACTION)
        whenever(storageService.addMeasurement(any())).thenReturn(Status.INVALID)

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        // then
                .andExpect(status().isBadRequest)
    }

    @Test
    fun noContentStatusForRejectedTransactions() {
        // setup
        val body = mapper.writeValueAsString(TRANSACTION)
        whenever(storageService.addMeasurement(any())).thenReturn(Status.REJECTED)

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        // then
                .andExpect(status().isNoContent)
                .andExpect(content().bytes(EMPTY_BODY))
    }

    @Test
    fun createdStatusForProcessedTransactions() {
        // setup
        val body = mapper.writeValueAsString(TRANSACTION)
        whenever(storageService.addMeasurement(any())).thenReturn(Status.PROCESSED)

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        // then
                .andExpect(status().isCreated)
                .andExpect(content().bytes(EMPTY_BODY))
    }

    @Test
    fun statisticsEndpoint() {
        // setup
        val stat = Statistic(3, 3.73, 10.237, 30.318)
        whenever(storageService.getStatistic()).thenReturn(stat)

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/statistics"))
        // then
                .andExpect(status().isOk)
                .andExpect(content().json(mapper.writeValueAsString(stat)))
    }

}