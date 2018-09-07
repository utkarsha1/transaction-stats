package com.utkarsha.transactionstats.domain;

import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {

    // The transaction amount
    private final BigDecimal amount;
    // The unix timestamp
    private final Instant timestamp;

    public Transaction(String amount, String timestamp) {
        this.amount = new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.timestamp = Instant.parse(timestamp);
    }

    /**
     * Simple getters, used in JSON <-> POJO conversion
     */

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}