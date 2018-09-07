package com.utkarsha.transactionstats.service;

import com.utkarsha.transactionstats.domain.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class implements an alternate strategy to store transactions support statistics calculations
 * We store all transactions in a cache, when we want to compute statistics we build/refresh
 * a new local cache which only has values that have a transaction timestamp > Instant.now() - 60 seconds
 * The cache is refreshed and stats recomputed when a GET /statistics request is received
 */
public class TransactionServiceV2 {

    private static ConcurrentHashMap<Instant, BigDecimal> allTranactionsMap = new ConcurrentHashMap<>();

    public static synchronized int add(String amount, String timestamp, long secondsThreshold) {
        int statusCode;
        Transaction transaction;
        try {
            transaction = new Transaction(amount, timestamp);
        } catch(Exception e) {
            statusCode = 422;
            return statusCode;
        }

        final Instant transactionTimestamp = transaction.getTimestamp();
        // If transaction is in the future we do not store it in any of our caches
        allTranactionsMap.put(transaction.getTimestamp(), transaction.getAmount());
        if(transactionTimestamp.isAfter(Instant.now())) {
            statusCode = 422;
        } else if (transactionTimestamp.isBefore(transactionTimestamp.minusSeconds(secondsThreshold + 1))) {
            statusCode = 204;
        } else {
            statusCode = 201;
        }
        return statusCode;
    }

    public static synchronized int add(String amount, String timestamp) {
        return add(amount, timestamp, 60);
    }

    public static synchronized int deleteAll() {
        allTranactionsMap.clear();

        return 204;
    }

    public static ConcurrentHashMap<Instant, BigDecimal> getAllTransactions() {
        return allTranactionsMap;
    }
}