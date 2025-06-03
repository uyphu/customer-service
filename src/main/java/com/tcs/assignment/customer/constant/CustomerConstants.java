package com.tcs.assignment.customer.constant;

import java.math.BigDecimal;

public final class CustomerConstants {
    public static final String TIER_SILVER = "Silver";
    public static final String TIER_GOLD = "Gold";
    public static final String TIER_PLATINUM = "Platinum";

    public static final BigDecimal GOLD_THRESHOLD = BigDecimal.valueOf(1000);
    public static final BigDecimal PLATINUM_THRESHOLD = BigDecimal.valueOf(10000);
    public static final int GOLD_MONTHS_LIMIT = 12;
    public static final int PLATINUM_MONTHS_LIMIT = 6;

    private CustomerConstants() {
        
    }
}
