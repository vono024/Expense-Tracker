package service;

import java.time.LocalDate;

public class TimeLimitService {

    public enum LimitType {DAILY, WEEKLY}

    private double dailyLimit;
    private double weeklyLimit;

    public void setLimit(LimitType type, double limit) {
        if (type == LimitType.DAILY) {
            this.dailyLimit = limit;
        } else if (type == LimitType.WEEKLY) {
            this.weeklyLimit = limit;
        }
    }

    public double getLimit(LimitType type) {
        if (type == LimitType.DAILY) {
            return dailyLimit;
        } else if (type == LimitType.WEEKLY) {
            return weeklyLimit;
        }
        return 0.0;
    }

    public boolean isLimitExceeded(LimitType type, double totalExpenseForPeriod) {
        if (type == LimitType.DAILY) {
            return totalExpenseForPeriod > dailyLimit;
        } else if (type == LimitType.WEEKLY) {
            return totalExpenseForPeriod > weeklyLimit;
        }
        return false;
    }

    public void clear() {
        this.dailyLimit = 0;
        this.weeklyLimit = 0;
    }
}
