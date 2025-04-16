package model;

public class Budget {
    private double monthlyLimit;
    private double currentSpending;

    public Budget(double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
        this.currentSpending = 0.0;
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }

    public double getCurrentSpending() {
        return currentSpending;
    }

    public void setMonthlyLimit(double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public void addSpending(double amount) {
        this.currentSpending += amount;
    }

    public void resetSpending() {
        this.currentSpending = 0.0;
    }

    public boolean isLimitExceeded() {
        return currentSpending > monthlyLimit;
    }
}
