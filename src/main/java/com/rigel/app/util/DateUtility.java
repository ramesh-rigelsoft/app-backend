package com.rigel.app.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtility {

    private DateUtility() {}

    public static LocalDateTime parseToDateTime(String dateStr, boolean isEnd) {

        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }

        dateStr = dateStr.trim();

        try {
            // 1️⃣ ISO Instant format: 2026-04-09T18:30:00.000Z
            if (dateStr.endsWith("Z")) {
                Instant instant = Instant.parse(dateStr);
                LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();

                return isEnd ? date.atTime(LocalTime.MAX) : date.atStartOfDay();
            }

            // 2️⃣ DateTime format: 2026-04-10T00:00 or 2026-04-10T00:00:00
            if (dateStr.contains("T")) {
                String clean = dateStr.substring(0, 16); // yyyy-MM-ddTHH:mm
                LocalDateTime ldt = LocalDateTime.parse(clean);

                LocalDate date = ldt.toLocalDate();
                return isEnd ? date.atTime(LocalTime.MAX) : date.atStartOfDay();
            }

            // 3️⃣ Only date: 2026-04-10
            LocalDate date = LocalDate.parse(dateStr);

            return isEnd ? date.atTime(LocalTime.MAX) : date.atStartOfDay();

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr, e);
        }
    }
}