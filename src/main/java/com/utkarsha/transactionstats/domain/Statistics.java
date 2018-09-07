package com.utkarsha.transactionstats.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Statistics {

    private String sum;
    private String avg;
    private String max;
    private String min;
    private int count;

    // We truncate stats. to have two decimal values and use the round half up strategy for truncation
    public Statistics(BigDecimal sum, BigDecimal avg, BigDecimal max, BigDecimal min, BigDecimal count) {
        this.sum = sum == null ? new BigDecimal("0.00")
            .setScale(2, BigDecimal.ROUND_HALF_UP).toString()
            : sum.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        this.max = max == null? new BigDecimal("0.00")
            .setScale(2, BigDecimal.ROUND_HALF_UP).toString()
            : max.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        this.min = min == null ? new BigDecimal("0.00")
            .setScale(2, BigDecimal.ROUND_HALF_UP).toString()
            : min.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        this.count = count == null ? 0 : Integer.parseInt(count.toString());
        this.avg = avg == null ? new BigDecimal("0.00")
            .setScale(2, BigDecimal.ROUND_HALF_UP).toString()
            : avg.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    public Statistics() {
        this.sum = "0.00";
        this.avg = "0.00";
        this.max = "0.00";
        this.min = "0.00";
        this.count = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Statistics that = (Statistics) o;
        return Objects.equals(sum, that.sum) &&
            Objects.equals(max, that.max) &&
            Objects.equals(min, that.min) &&
            Objects.equals(count, that.count) &&
            Objects.equals(avg, that.avg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sum, max, min, count, avg);
    }

    @Override
    public String toString() {
        return "Statistics{" +
            "sum=" + sum +
            ", max=" + max +
            ", min=" + min +
            ", count=" + count +
            ", avg=" + avg +
            '}';
    }

    /***
     * Simple getters, will be used in JSON <-> POJO conversion
     */
    public String getSum() {
        return sum;
    }

    public String getAvg() {
        return avg;
    }

    public String getMax() {
        return max;
    }

    public String getMin() {
        return min;
    }

    public int getCount() {
        return count;
    }

}