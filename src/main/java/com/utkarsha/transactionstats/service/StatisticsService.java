package com.utkarsha.transactionstats.service;

import com.utkarsha.transactionstats.domain.Statistics;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsService {

    /** Ensure that the statistics calculations are synchronized,
     * stats calculation has have fetch-update-get pattern.
     * All transactions are fetched from the recentTransactionsMap.
     * Where, every transaction timestamp is between now
     * and now - specified seconds (60 default), the trasaction amount
     * is added to sum and count is incremented as well as average re-calculated.
     * The min/max values are re-computed and updated.
     **/
    public static synchronized HashMap<String, BigDecimal> computeStatistics() {
        ConcurrentHashMap<Instant, BigDecimal> recentTransactionsMap = TransactionService.getRecentTransactions();
        HashMap<String, BigDecimal> statisticsMap = new HashMap<>();

        // Re-calculate stats, for all transactions over the last 60 seconds
        for (Instant instant : recentTransactionsMap.keySet()) {
                statisticsMap.merge("sum", recentTransactionsMap.get(instant), BigDecimal::add);
                statisticsMap.merge("count", new BigDecimal(1), BigDecimal::add);
                statisticsMap.merge("min", recentTransactionsMap.get(instant), BigDecimal::min);
                statisticsMap.merge("max", recentTransactionsMap.get(instant), BigDecimal::max);
                statisticsMap.put("avg", statisticsMap.get("sum")
                    .divide(statisticsMap.get("count"), 2, BigDecimal.ROUND_HALF_UP));
        }
        return statisticsMap;
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
