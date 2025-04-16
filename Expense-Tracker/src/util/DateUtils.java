package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(LocalDate date) {
        return date.format(formatter);
    }

    public static LocalDate parse(String text) {
        return LocalDate.parse(text, formatter);
    }

    public static boolean isSameMonth(LocalDate d1, LocalDate d2) {
        return d1.getMonthValue() == d2.getMonthValue() && d1.getYear() == d2.getYear();
    }

    public static boolean isToday(LocalDate date) {
        return LocalDate.now().equals(date);
    }

    public static boolean isBeforeToday(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }
}
