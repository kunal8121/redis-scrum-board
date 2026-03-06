package com.redisscrumboard.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeFormat {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static String format(Instant instant) {
        var formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(ZoneId.from(ZoneOffset.UTC));
        return formatter.format(instant);
    }
}
