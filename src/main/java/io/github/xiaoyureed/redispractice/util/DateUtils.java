package io.github.xiaoyureed.redispractice.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author EX-XIAOYU003
 * Date: 2019-8-2
 */
public class DateUtils {
    public static final String DEFAULT_PATTER = "yyyy-MM-dd HH:mm:ss";

    public static String format(String pattern, LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_PATTER));
    }

    public static LocalDateTime deFormat(String pattern, String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime deFormat(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(DEFAULT_PATTER));
    }

    // public static int nano(LocalDateTime dateTime) {
    // }
    //
    // public static LocalDateTime deNano(int nano) {
    //     new Instant()
    // }
}
