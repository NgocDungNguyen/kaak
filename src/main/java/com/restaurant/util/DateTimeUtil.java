package com.restaurant.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    /**
     * Convert LocalDateTime to formatted string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(formatter);
    }

    /**
     * Parse string to LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return null;
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    /**
     * Get current date time as formatted string
     */
    public static String getCurrentDateTime() {
        return formatDateTime(LocalDateTime.now());
    }

    /**
     * Calculate estimated delivery time (current time + 45 minutes)
     */
    public static LocalDateTime calculateEstimatedDeliveryTime() {
        return LocalDateTime.now().plusMinutes(45);
    }
}