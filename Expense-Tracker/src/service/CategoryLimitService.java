package service;

import model.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryLimitService {
    private final Map<String, Double> categoryLimits = new HashMap<>();

    public void setLimit(String category, double limit) {
        categoryLimits.put(category, limit);
    }

    public double getLimit(String category) {
        return categoryLimits.getOrDefault(category, 0.0);
    }

    public boolean isLimitExceeded(String category, List<Transaction> transactions) {
        double total = getTotalForCategory(category, transactions);
        double limit = getLimit(category);
        return total > limit && limit > 0;
    }

    public double getTotalForCategory(String category, List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getType().equals("expense"))
                .filter(t -> t.getCategory().equals(category))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Map<String, Double> getAllLimits() {
        return categoryLimits;
    }

    public void clearLimits() {
        categoryLimits.clear();
    }
}
