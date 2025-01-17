// OrderNumberGenerator.java
package com.restaurant.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderNumberGenerator {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final AtomicInteger sequence = new AtomicInteger(1);

    /**
     * Generate unique order number
     * Format: yyyyMMdd-sequence (e.g., 20250116-001)
     */
    public static String generateOrderNumber() {
        String date = LocalDateTime.now().format(DATE_FORMAT);
        String seq = String.format("%03d", sequence.getAndIncrement());
        return date + "-" + seq;
    }

    /**
     * Reset sequence counter (typically called at start of new day)
     */
    public static void resetSequence() {
        sequence.set(1);
    }
}