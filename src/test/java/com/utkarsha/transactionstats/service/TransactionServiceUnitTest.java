package com.utkarsha.transactionstats.service;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionServiceUnitTest {

    @After
    public void tearDown() {
        TransactionService.deleteAll();
    }

    @Test
    public void testAddSingleRecentTransction() {
        String timestamp = Instant.now().minusSeconds(1).toString();
        String amount = "234.78";
        int code = TransactionService.add(amount, timestamp);
        ConcurrentHashMap recentTransactionsMap = TransactionService.getRecentTransactions();

        assertEquals("Did not find expected transaction in recent store", 1, recentTransactionsMap.size());
        assertEquals("Amount not as expected", new BigDecimal(amount), recentTransactionsMap.get(Instant.parse(timestamp)));
        assertEquals("Incorrect Status Code", 201, code);

        TransactionService.deleteAll();
        assertEquals("DeleteAll does not work, found recent transactions", 0, TransactionService.getRecentTransactions().size());
        assertEquals("DeleteAll does not work, found older transactions", 0, TransactionService.getOlderTransactions().size());
    }

    @Test
    public void testRecentTransctionNotAddedToOlderMap() {
        String timestamp = Instant.now().toString();
        String amount = "234.78";
        TransactionService.add(amount, timestamp);
        ConcurrentHashMap olderTransactionsMap = TransactionService.getOlderTransactions();
        assertEquals("Older transaction map store should be empty", 0, olderTransactionsMap.size());
        TransactionService.deleteAll();
        assertEquals("DeleteAll does not work, found recent transactions", 0, TransactionService.getRecentTransactions().size());
        assertEquals("DeleteAll does not work, found older transactions", 0, TransactionService.getOlderTransactions().size());
    }

    @Test
    public void testAddSingleOlderTransction() {
        String timestamp = "2018-07-17T09:59:51.312Z";
        String amount = "34.78";
        int code = TransactionService.add(amount, timestamp);
        ConcurrentHashMap olderTransactionsMap = TransactionService.getOlderTransactions();

        assertEquals("Did not find expected transaction in older store", 1, olderTransactionsMap.size());
        assertEquals("Amount not as expected", new BigDecimal(amount), olderTransactionsMap.get(Instant.parse(timestamp)));
        assertEquals("Incorrect return code", 204, code);

        TransactionService.deleteAll();
        assertEquals("DeleteAll does not work, found recent transactions", 0, TransactionService.getRecentTransactions().size());
        assertEquals("DeleteAll does not work, found older transactions", 0, TransactionService.getOlderTransactions().size());
    }

    @Test
    public void testOlderTransctionNotAddedToRecentMap() {
        String timestamp = "2018-07-17T09:59:51.312Z";
        String amount = "34.78";
        TransactionService.add(amount, timestamp);
        ConcurrentHashMap recentTransactionsMap = TransactionService.getRecentTransactions();
        assertEquals("Recent transaction store should be empty", 0, recentTransactionsMap.size());
        TransactionService.deleteAll();
        assertEquals("DeleteAll does not work, found recent transactions", 0, TransactionService.getRecentTransactions().size());
        assertEquals("DeleteAll does not work, found older transactions", 0, TransactionService.getOlderTransactions().size());
    }

    @Test
    public void testAddMultipleTransactions() {
        Instant now = Instant.now();
        TransactionService.add("12.12", now.minusSeconds(15).toString());
        TransactionService.add("13.12", now.minusSeconds(30).toString());
        TransactionService.add("14.12", now.minusSeconds(59).toString());
        TransactionService.add("15.12", now.minusSeconds(61).toString());
        TransactionService.add("16.12", now.minusSeconds(62).toString());

        ConcurrentHashMap recentTransactionsMap = TransactionService.getRecentTransactions();
        ConcurrentHashMap olderTransactionsMap = TransactionService.getOlderTransactions();

        assertEquals("Recent transaction store not as expected", 3, recentTransactionsMap.size());
        assertEquals("Older transaction store not as expected", 2, olderTransactionsMap.size());

        int code = TransactionService.deleteAll();
        assertEquals("DeleteAll does not work, found recent transactions", 0, TransactionService.getRecentTransactions().size());
        assertEquals("DeleteAll does not work, found older transactions", 0, TransactionService.getOlderTransactions().size());
        assertEquals("Unexpected delete return code", 204, code);
    }

    @Test
    public void testUnparseableInputsForAddTransaction() {
        int code = TransactionService.add(null, null);
        assertEquals(422, code);

        code = TransactionService.add("", "");
        assertEquals(422, code);

        code = TransactionService.add("A@%&XXdfdf", "~~`><jkhhkAQWE%%&*");
        assertEquals(422, code);
    }

    @Test
    public void testFutureInputTimestampForAddTransaction() {
        final Instant futureTimestamp = Instant.now().plusSeconds(120);
        int code = TransactionService.add("234.56", futureTimestamp.toString());
        assertEquals(422, code);

        assertEquals(null, TransactionService.getOlderTransactions().get(futureTimestamp));
        assertEquals(null, TransactionService.getRecentTransactions().get(futureTimestamp));
    }

    @Test
    public void testDeleteAll() {
        Instant now = Instant.now();
        TransactionService.add("12.12", now.minusSeconds(15).toString());
        TransactionService.add("13.12", now.minusSeconds(30).toString());
        TransactionService.add("14.12", now.minusSeconds(59).toString());
        TransactionService.add("15.12", now.minusSeconds(61).toString());
        TransactionService.add("16.12", now.minusSeconds(62).toString());

        assertEquals("Recent transaction store not as expected", 3, TransactionService.getRecentTransactions().size());
        assertEquals("Older transaction store not as expected", 2, TransactionService.getOlderTransactions().size());

        int code = TransactionService.deleteAll();
        assertEquals("DeleteAll does not work, found recent transactions", 0, TransactionService.getRecentTransactions().size());
        assertEquals("DeleteAll does not work, found older transactions", 0, TransactionService.getOlderTransactions().size());
        assertEquals("Unexpected delete return code", 204, code);
    }
}