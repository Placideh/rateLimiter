package com.placideh.rateLimiter.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public static String getCurrentYearMonth() {
        return LocalDateTime.now().format(MONTH_FORMATTER);
    }

    private DateTimeUtil() {}
}
