package com.example.nio.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Log {

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static void info(String content) {
        System.out.println(FORMATTER.format(LocalDateTime.now()) + " -> [ info] " + content);
    }
}
