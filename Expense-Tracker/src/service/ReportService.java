package service;

import model.Transaction;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {
    public double getTotalByType(List<Transaction> transactions, String type) {
        return transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Map<String, Double> getTotalByCategory(List<Transaction> transactions, String type) {
        Map<String, Double> totals = new HashMap<>();
        for (Transaction t : transactions) {
            if (t.getType().equalsIgnoreCase(type)) {
                totals.put(t.getCategory(), totals.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            }
        }
        return totals;
    }

    public double getTotalForMonth(List<Transaction> transactions, int month, int year, String type) {
        return transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .filter(t -> t.getDate().getMonthValue() == month && t.getDate().getYear() == year)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Map<String, Double> getCategorySummaryForMonth(List<Transaction> transactions, int month, int year, String type) {
        Map<String, Double> summary = new HashMap<>();
        for (Transaction t : transactions) {
            if (t.getType().equalsIgnoreCase(type) &&
                    t.getDate().getMonthValue() == month &&
                    t.getDate().getYear() == year) {
                summary.put(t.getCategory(), summary.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            }
        }
        return summary;
    }
}
