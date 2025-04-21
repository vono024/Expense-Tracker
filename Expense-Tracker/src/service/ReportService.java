package service;

import model.Transaction;

import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    public double getTotalByType(List<Transaction> list, String type) {
        return list.stream()
                .filter(t -> t.getType().equals(type))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalByCategory(List<Transaction> list, String category) {
        return list.stream()
                .filter(t -> t.getCategory().equals(category))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalForDate(List<Transaction> list, java.time.LocalDate date) {
        return list.stream()
                .filter(t -> t.getDate().equals(date))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalForWeek(List<Transaction> list, java.time.LocalDate date) {
        java.time.DayOfWeek firstDayOfWeek = java.time.DayOfWeek.MONDAY;
        java.time.LocalDate start = date.with(java.time.temporal.TemporalAdjusters.previousOrSame(firstDayOfWeek));
        java.time.LocalDate end = start.plusDays(6);
        return list.stream()
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getBalance(List<Transaction> list) {
        double income = getTotalByType(list, "income");
        double expense = getTotalByType(list, "expense");
        return income - expense;
    }

    public double getAverageTransaction(List<Transaction> list, String type) {
        List<Transaction> filtered = list.stream()
                .filter(t -> t.getType().equals(type))
                .collect(Collectors.toList());

        return filtered.stream()
                .mapToDouble(Transaction::getAmount)
                .average()
                .orElse(0);
    }

    public Map<String, Double> getGroupedCategoryTotals(List<Transaction> list, String type) {
        return list.stream()
                .filter(t -> t.getType().equals(type))
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)));
    }

    public List<Transaction> getTopTransactions(List<Transaction> list, String type, int limit) {
        return list.stream()
                .filter(t -> t.getType().equals(type))
                .sorted((a, b) -> Double.compare(b.getAmount(), a.getAmount()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
