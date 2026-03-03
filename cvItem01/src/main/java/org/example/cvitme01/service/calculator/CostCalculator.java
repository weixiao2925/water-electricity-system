package org.example.cvitme01.service.calculator;

import java.math.BigDecimal;

public abstract class CostCalculator {

    protected abstract TierInfo getTierInfo(String type, int seq);

    public BigDecimal calculateCost(String type, BigDecimal value) {
        TierInfo tier1 = getTierInfo(type, 1);
        TierInfo tier2 = getTierInfo(type, 2);
        TierInfo tier3 = getTierInfo(type, 3);

        if (tier1 == null || tier2 == null || tier3 == null) {
            return null;
        }

        return computeTieredCost(value, tier1, tier2, tier3);
    }

    private BigDecimal computeTieredCost(BigDecimal value, TierInfo tier1, TierInfo tier2, TierInfo tier3) {
        BigDecimal cost = BigDecimal.ZERO;
        BigDecimal remaining = value;

        // 第一阶梯
        if (remaining.compareTo(tier1.getUpperBound()) <= 0) {
            return remaining.multiply(tier1.getPrice());
        }
        cost = tier1.getUpperBound().multiply(tier1.getPrice());
        remaining = remaining.subtract(tier1.getUpperBound());

        // 第二阶梯
        if (remaining.compareTo(tier2.getUpperBound()) <= 0) {
            return cost.add(remaining.multiply(tier2.getPrice()));
        }
        cost = cost.add(tier2.getUpperBound().multiply(tier2.getPrice()));
        remaining = remaining.subtract(tier2.getUpperBound());

        // 第三阶梯
        cost = cost.add(remaining.multiply(tier3.getPrice()));

        return cost;
    }

    public static class TierInfo {
        private final BigDecimal upperBound;
        private final BigDecimal price;

        public TierInfo(BigDecimal upperBound, BigDecimal price) {
            this.upperBound = upperBound;
            this.price = price;
        }

        public BigDecimal getUpperBound() { return upperBound; }
        public BigDecimal getPrice() { return price; }
    }
}
