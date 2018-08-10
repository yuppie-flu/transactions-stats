package com.github.yuppieflu.stats.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yuppieflu.stats.rest.dto.Transaction;
import com.github.yuppieflu.stats.service.StorageService;
import com.github.yuppieflu.stats.service.domain.Measurement;
import com.github.yuppieflu.stats.service.domain.Statistic;
import com.github.yuppieflu.stats.service.domain.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionsController.class)
public class TransactionsControllerTest {

    private static final byte[] EMPTY_BODY = new byte[0];
    private static final Transaction TRANSACTION = new Transaction(System.currentTimeMillis(), 1.0);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageService storageService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void badRequestStatusForFutureTransactions() throws Exception {
        // setup
        String body = mapper.writeValueAsString(TRANSACTION);
        when(storageService.addMeasurement(any(Measurement.class))).thenReturn(Status.INVALID);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(body))
               // then
               .andExpect(status().isBadRequest());
    }

    @Test
    public void noContentStatusForRejectedTransactions() throws Exception {
        // setup
        String body = mapper.writeValueAsString(TRANSACTION);
        when(storageService.addMeasurement(any(Measurement.class))).thenReturn(Status.REJECTED);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(body))
        // then
               .andExpect(status().isNoContent())
               .andExpect(content().bytes(EMPTY_BODY));
    }

    @Test
    public void createdStatusForProcessedTransactions() throws Exception {
        // setup
        String body = mapper.writeValueAsString(TRANSACTION);
        when(storageService.addMeasurement(any(Measurement.class))).thenReturn(Status.PROCESSED);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(body))
        // then
               .andExpect(status().isCreated())
               .andExpect(content().bytes(EMPTY_BODY));
    }

    @Test
    public void statisticsEndpoint() throws Exception {
        // setup

        Statistic stat = new Statistic(3, 3.73, 10.237, 30.318);
        when(storageService.getStatistic()).thenReturn(stat);

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/statistics"))

        // then
               .andExpect(status().isOk())
               .andExpect(content().json(mapper.writeValueAsString(stat)));
    }
}
