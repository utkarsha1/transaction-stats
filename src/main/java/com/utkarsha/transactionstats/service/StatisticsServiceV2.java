package com.utkarsha.transactionstats.service;

import com.utkarsha.transactionstats.domain.Statistics;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsServiceV2 {

    private static ConcurrentHashMap<Instant, BigDecimal> cache = new ConcurrentHashMap<>();

    public static synchronized HashMap<String, BigDecimal> computeStatistics() {
        refreshCache(Instant.now());

        HashMap<String, BigDecimal> statisticsMap = new HashMap<>();
        for (Instant instant : cache.keySet()) {
                statisticsMap.merge("sum", cache.get(instant), BigDecimal::add);
                statisticsMap.merge("count", new BigDecimal(1), BigDecimal::add);
                statisticsMap.merge("min", cache.get(instant), BigDecimal::min);
                statisticsMap.merge("max", cache.get(instant), BigDecimal::max);
                statisticsMap.put("avg", statisticsMap.get("sum")
                    .divide(statisticsMap.get("count"), 2, BigDecimal.ROUND_HALF_UP));
        }
        return statisticsMap;
    }

    private static void refreshCache(Instant timestamp) {
        // remove old entries from cache as well as any entries with timestamp in the future
        for(ConcurrentHashMap.Entry<Instant, BigDecimal> entry : cache.entrySet()) {
            if(entry.getKey().isBefore(timestamp.minusSeconds(60)) && entry.getKey().isAfter(timestamp)) {
                cache.remove(entry.getKey(), entry.getValue());
            }
        }

        // add valid entries to cache if not present
        for(ConcurrentHashMap.Entry<Instant, BigDecimal> entry : TransactionServiceV2.getAllTransactions().entrySet()) {
            if(entry.getKey().isAfter(timestamp.minusSeconds(60)) && entry.getKey().isBefore(timestamp)) {
               cache.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
    }

    public static synchronized Statistics getStatistics() {
        HashMap<String, BigDecimal> statisticsMap = computeStatistics();

        return new Statistics(statisticsMap.get("sum"),
            statisticsMap.get("avg"),
            statisticsMap.get("max"),
            statisticsMap.get("min"),
            statisticsMap.get("count"));
    }
}
