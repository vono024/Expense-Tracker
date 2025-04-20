package service;

import model.Transaction;
import util.DateUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private CurrencyService currencyService = new CurrencyService();

    public double getTotalByType(List<Transaction> list, String type) {
        return list.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .mapToDouble(t -> {
                    double rate = currencyService.getRate(t.getCurrency());
                    System.out.println("DEBUG → " + t.getAmount() + " " + t.getCurrency() + " → курс: " + rate);
                    return t.getAmount() * rate;
                })
                .sum();
    }

    public double getCategorySum(List<Transaction> list, String category) {
        return list.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .mapToDouble(t -> t.getAmount() * currencyService.getRate(t.getCurrency()))
                .sum();
    }

    public Map<String, Double> getGroupedCategoryTotals(List<Transaction> list, String type) {
        return list.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(t -> t.getAmount() * currencyService.getRate(t.getCurrency()))
                ));
    }

    public double getTotalForCurrentMonth(List<Transaction> list, String type) {
        LocalDate now = LocalDate.now();
        return list.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .filter(t -> DateUtils.isSameMonth(t.getDate(), now))
                .mapToDouble(t -> t.getAmount() * currencyService.getRate(t.getCurrency()))
                .sum();
    }

    public double getAverageTransaction(List<Transaction> list, String type) {
        return list.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .mapToDouble(t -> t.getAmount() * currencyService.getRate(t.getCurrency()))
                .average()
                .orElse(0.0);
    }

    public List<Transaction> getTopTransactions(List<Transaction> list, String type, int topN) {
        return list.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .sorted(Comparator.comparingDouble(t -> -t.getAmount() * currencyService.getRate(t.getCurrency())))
                .limit(topN)
                .collect(Collectors.toList());
    }

    public double getBalance(List<Transaction> list) {
        double income = getTotalByType(list, "income");
        double expense = getTotalByType(list, "expense");
        return income - expense;
    }
}
