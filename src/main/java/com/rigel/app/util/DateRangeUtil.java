package com.rigel.app.util;


import java.time.*;
import java.time.temporal.TemporalAdjusters;

public class DateRangeUtil {

    public record DateRange(
            ZonedDateTime start,
            ZonedDateTime end,
            long startEpochMilli,
            long endEpochMilli
    ) {}

    public static DateRange getDateRange(String cycle) {

        ZoneId zoneId = ZoneId.of("Asia/Kolkata"); // Fixed timezone
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        ZonedDateTime start;
        ZonedDateTime end;

        switch (cycle.toUpperCase()) {

            case "DAILY" -> {
                start = now.toLocalDate().atStartOfDay(zoneId);
                end = now.toLocalDate().atTime(LocalTime.MAX).atZone(zoneId);
            }

            case "MONTHLY" -> {
                LocalDate firstDay = now.toLocalDate()
                        .with(TemporalAdjusters.firstDayOfMonth());
                LocalDate lastDay = now.toLocalDate()
                        .with(TemporalAdjusters.lastDayOfMonth());

                start = firstDay.atStartOfDay(zoneId);
                end = lastDay.atTime(LocalTime.MAX).atZone(zoneId);
            }

            case "YEARLY" -> {
                LocalDate firstDay = now.toLocalDate()
                        .with(TemporalAdjusters.firstDayOfYear());
                LocalDate lastDay = now.toLocalDate()
                        .with(TemporalAdjusters.lastDayOfYear());

                start = firstDay.atStartOfDay(zoneId);
                end = lastDay.atTime(LocalTime.MAX).atZone(zoneId);
            }

            default -> throw new IllegalArgumentException(
                    "Invalid cycle. Use DAILY, MONTHLY or YEARLY");
        }

        return new DateRange(
                start,
                end,
                start.toInstant().toEpochMilli(),
                end.toInstant().toEpochMilli()
        );
    }
}
