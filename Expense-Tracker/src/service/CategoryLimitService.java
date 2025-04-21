package service;

import java.util.HashMap;
import java.util.Map;

public class CategoryLimitService {

    private Map<String, Double> categoryLimits = new HashMap<>();

    public CategoryLimitService() {
        // Initial values (if necessary)
        categoryLimits.put("Їжа", 500.0);
        categoryLimits.put("Транспорт", 1000.0);
    }

    public void setLimit(String category, double limit) {
        categoryLimits.put(category, limit);
    }

    public double getLimit(String category) {
        return categoryLimits.getOrDefault(category, 0.0);
    }

    public Map<String, Double> getAllLimits() {
        return categoryLimits;
    }

    // Перевірка, чи перевищено ліміт для категорії
    public boolean isLimitExceeded(String category, double amount) {
        double currentLimit = getLimit(category);
        return currentLimit >= 0 && (amount > currentLimit);
    }

    // Метод для очищення лімітів категорій
    public void clear() {
        categoryLimits.clear();
    }
}
