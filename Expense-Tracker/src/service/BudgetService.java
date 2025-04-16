package service;

import model.Budget;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class BudgetService {
    private final Map<YearMonth, Budget> budgets = new HashMap<>();

    public void setMonthlyLimit(double amount) {
        YearMonth current = YearMonth.now();
        budgets.put(current, new Budget(current, amount));
    }

    public double getMonthlyLimit() {
        YearMonth current = YearMonth.now();
        Budget budget = budgets.get(current);
        return budget != null ? budget.getLimit() : 0.0;
    }

    public boolean isLimitExceeded(double expensesThisMonth) {
        double limit = getMonthlyLimit();
        return limit > 0 && expensesThisMonth > limit;
    }

    public Budget getBudgetForCurrentMonth() {
        YearMonth current = YearMonth.now();
        return budgets.getOrDefault(current, new Budget(current, 0.0));
    }

    public Map<YearMonth, Budget> getAllBudgets() {
        return new HashMap<>(budgets);
    }

    public void clear() {
        budgets.clear();
    }
}
