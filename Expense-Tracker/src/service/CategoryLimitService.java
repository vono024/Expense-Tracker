package service;

import java.util.HashMap;
import java.util.Map;

public class CategoryLimitService {

    private Map<String, Double> categoryLimits = new HashMap<>();

    public void setLimit(String category, double limit) {
        categoryLimits.put(category, limit);
    }

    public double getLimit(String category) {
        return categoryLimits.getOrDefault(category, 0.0);
    }

    public Map<String, Double> getAllLimits() {
        return categoryLimits;
    }

    public boolean isLimitExceeded(String category, double amount) {
        double currentLimit = getLimit(category);
        return currentLimit >= 0 && (amount > currentLimit);
    }

    public void clear() {
        categoryLimits.clear();
    }
}
