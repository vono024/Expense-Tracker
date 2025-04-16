package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(LocalDate date) {
        return date.format(formatter);
    }

    public static LocalDate parse(String dateString) {
        return LocalDate.parse(dateString, formatter);
    }
}
