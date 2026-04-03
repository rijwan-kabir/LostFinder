package com.diu.lostfinder.config;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SqlLogger {

    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static void log(String operation, String sql, Object... params) {
        String time = LocalTime.now().format(timeFormat);
        String cleanSql = sql.replaceAll("\\s+", " ").trim();

        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ [" + time + "] " + operation);
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│ SQL: " + cleanSql);

        if (params.length > 0 && params[0] != null) {
            System.out.println("├─────────────────────────────────────────────────────────┤");
            System.out.print("│ Parameters: ");
            for (int i = 0; i < params.length; i++) {
                if (params[i] != null) {
                    String value = params[i].toString();
                    if (value.length() > 40) {
                        value = value.substring(0, 37) + "...";
                    }
                    System.out.print(value);
                    if (i < params.length - 1) System.out.print(", ");
                }
            }
            System.out.println();
        }
        System.out.println("└─────────────────────────────────────────────────────────┘");
    }
}