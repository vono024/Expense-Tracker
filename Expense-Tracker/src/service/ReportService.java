package service;

import model.Transaction;
import util.DateUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    public double getTotalByType(List<Transaction> list, String type) {
        return list.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getCategorySum(List<Transaction> list, String category) {
        return list.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Map<String, Double> getGroupedCategoryTotals(List<Transaction> list, String type) {
        return list.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    public double getTotalForCurrentMonth(List<Transaction> list, String type) {
        LocalDate now = LocalDate.now();
        return list.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .filter(t -> DateUtils.isSameMonth(t.getDate(), now))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getAverageTransaction(List<Transaction> list, String type) {
        return list.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .mapToDouble(Transaction::getAmount)
                .average()
                .orElse(0.0);
    }

    public List<Transaction> getTopTransactions(List<Transaction> list, String type, int topN) {
        return list.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .sorted(Comparator.comparingDouble(Transaction::getAmount).reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    public double getBalance(List<Transaction> list) {
        return getTotalByType(list, "income") - getTotalByType(list, "expense");
    }

    public List<Transaction> getTodayTransactions(List<Transaction> list) {
        return list.stream()
                .filter(t -> DateUtils.isToday(t.getDate()))
                .collect(Collectors.toList());
    }

    public Map<LocalDate, Double> getDailyExpenseSummary(List<Transaction> list) {
        return list.stream()
                .filter(t -> t.getType().equalsIgnoreCase("expense"))
                .collect(Collectors.groupingBy(
                        Transaction::getDate,
                        TreeMap::new,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }
}
