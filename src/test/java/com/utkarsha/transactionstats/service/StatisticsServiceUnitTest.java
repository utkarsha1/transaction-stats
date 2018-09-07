package com.utkarsha.transactionstats.service;

import static org.junit.Assert.assertEquals;

import com.utkarsha.transactionstats.domain.Statistics;
import org.junit.After;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;

public class StatisticsServiceUnitTest {

    @After
    public void tearDown() {
        TransactionService.deleteAll();
    }

    @Test
    public void testStatisticsCalculationsForMultipleTransactions() {
        Instant now = Instant.now();

        TransactionService.add("12.12", now.toString());
        TransactionService.add("13.12", now.minusSeconds(30).toString());
        TransactionService.add("14.12", now.minusSeconds(59).toString());
        TransactionService.add("15.12", now.minusSeconds(60).toString());
        TransactionService.add("16.12", now.minusSeconds(61).toString());

        Statistics actual = StatisticsService.getStatistics();

        Statistics expected = new Statistics(new BigDecimal("39.36"), new BigDecimal("13.12"), new BigDecimal("14.12"), new BigDecimal("12.12"), new BigDecimal("3"));

        assertEquals(expected, actual);

        TransactionService.deleteAll();
        assertEquals("DeleteAll does not work, found recent transactions", 0, TransactionService.getRecentTransactions().size());
        assertEquals("DeleteAll does not work, found older transactions", 0, TransactionService.getOlderTransactions().size());
    }

    @Test
    public void testReturnEmptyStatisticsNoTransactionsReceived() {
        TransactionService.deleteAll();
        assertEquals(new Statistics(), StatisticsService.getStatistics());
    }
}