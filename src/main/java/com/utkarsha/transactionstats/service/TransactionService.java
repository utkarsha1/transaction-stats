package com.utkarsha.transactionstats.service;

import com.utkarsha.transactionstats.domain.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionService {

    /**
     * The Java volatile keyword guarantees visibility of changes to variables across threads
     * The maps we use for storing transactions and calculating stats are shared objects amongst
     * multiple concurrent threads. Multi-threaded services may often come to a situation where
     * multiple threads try to access the same resources and finally produce erroneous and
     * unforeseen results
     */
    // Stores transactions posted with a timestamp such that timestamp <= Instant.now() - 60
    private static volatile ConcurrentHashMap<Instant, BigDecimal> olderTransactionsMap = new ConcurrentHashMap<>();
    // Stores transactions posted with a timestamp such that Instant.now() - 60 < timestamp <= Instant.now()
    // This is the cache we use for statistics calculations
    private static volatile ConcurrentHashMap<Instant, BigDecimal> recentTransactionsMap = new ConcurrentHashMap<>();
    // We store all transactions that are posted to our service, transactions in allTranactionsMap are not used
    // but can be used to recover transactions if the caches are lost/corrupted
    private static ConcurrentHashMap<Instant, BigDecimal> allTranactionsMap = new ConcurrentHashMap<>();

    /**
     * Since the recentTransactionsMap (and olderTransactionMap) is visible to more than one threads,
     * all reads or writes to the recentTransactionsMap fields are done through synchronized methods.
     * Two or more invocations of synchronized methods cannot interleave. If one thread is executing
     * a synchronized method, all others threads that invoke synchronized methods on the same Object
     * will be forced to wait until first thread is done with the Object. This ensures that the service
     * can handle multiple concurrent requests
     */
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
        } else if(transactionTimestamp.isAfter(Instant.now().minusSeconds(secondsThreshold))
            && transactionTimestamp.isBefore(Instant.now()) || transactionTimestamp.equals(Instant.now())) {
            recentTransactionsMap.put(transaction.getTimestamp(), transaction.getAmount());
            statusCode = 201;
        } else {
            olderTransactionsMap.put(transaction.getTimestamp(), transaction.getAmount());
            statusCode = 204;
        }
        return statusCode;
    }

    public static synchronized int add(String amount, String timestamp) {
        return add(amount, timestamp, 60);
    }

    public static synchronized int deleteAll() {

        recentTransactionsMap.clear();
        olderTransactionsMap.clear();

        return 204;
    }

    public static synchronized ConcurrentHashMap<Instant, BigDecimal> getRecentTransactions() {
        return recentTransactionsMap;
    }

    public static synchronized ConcurrentHashMap<Instant, BigDecimal> getOlderTransactions() {
        return olderTransactionsMap;
    }

    public static ConcurrentHashMap<Instant, BigDecimal> getAllTransactions() {
        return allTranactionsMap;
    }
}