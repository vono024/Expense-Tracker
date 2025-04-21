package service;

import java.util.HashMap;
import java.util.Map;

public class TimeLimitService {

    public enum LimitType {
        DAILY, WEEKLY
    }

    private Map<LimitType, Double> limits = new HashMap<>();

    public TimeLimitService() {
        // Initial values (if necessary)
        limits.put(LimitType.DAILY, 1000.0);
        limits.put(LimitType.WEEKLY, 10000.0);
    }

    public double getLimit(LimitType type) {
        return limits.getOrDefault(type, 0.0);
    }

    public void setLimit(LimitType type, double value) {
        limits.put(type, value);
    }

    // Перевірка, чи перевищено ліміт часу (денний або тижневий)
    public boolean isLimitExceeded(LimitType type, double amount) {
        double currentLimit = getLimit(type);
        return currentLimit >= 0 && (amount > currentLimit);
    }

    // Метод для очищення лімітів часу
    public void clear() {
        limits.clear();
    }
}
