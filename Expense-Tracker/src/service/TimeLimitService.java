package service;

import model.Transaction;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

public class TimeLimitService {

    public enum LimitType {
        DAILY, WEEKLY
    }

    private double dailyLimit = 0;
    private double weeklyLimit = 0;

    public void setLimit(LimitType type, double limit) {
        if (type == LimitType.DAILY) dailyLimit = limit;
        else weeklyLimit = limit;
    }

    public double getLimit(LimitType type) {
        return (type == LimitType.DAILY) ? dailyLimit : weeklyLimit;
    }

    public boolean isLimitExceeded(LimitType type, List<Transaction> transactions) {
        double total = (type == LimitType.DAILY)
                ? getTodayTotal(transactions)
                : getThisWeekTotal(transactions);
        double limit = getLimit(type);
        return limit > 0 && total > limit;
    }

    public double getTodayTotal(List<Transaction> transactions) {
        LocalDate today = LocalDate.now();
        return transactions.stream()
                .filter(t -> t.getType().equals("expense"))
                .filter(t -> t.getDate().equals(today))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getThisWeekTotal(List<Transaction> transactions) {
        LocalDate now = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeek = now.get(weekFields.weekOfWeekBasedYear());
        int currentYear = now.getYear();

        return transactions.stream()
                .filter(t -> t.getType().equals("expense"))
                .filter(t -> {
                    LocalDate date = t.getDate();
                    int week = date.get(weekFields.weekOfWeekBasedYear());
                    int year = date.getYear();
                    return week == currentWeek && year == currentYear;
                })
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}
