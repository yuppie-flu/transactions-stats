package com.github.yuppieflu.stats;

import com.github.yuppieflu.stats.rest.dto.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    public void addTransactionTest() {
        // setup
        Transaction transaction = new Transaction(System.currentTimeMillis(), 1.0);

        // when
        ResponseEntity<Void> responseEntity = testRestTemplate.postForEntity("/transactions", transaction, Void.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
