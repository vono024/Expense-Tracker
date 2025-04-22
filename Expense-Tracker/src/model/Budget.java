package model;

import java.time.YearMonth;
import java.util.Objects;

public class Budget {
    private YearMonth month;
    private double limit;

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Budget)) return false;
        Budget budget = (Budget) o;
        return Objects.equals(month, budget.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month);
    }

    @Override
    public String toString() {
        return "Ліміт на " + month + ": " + limit + " грн";
    }
}
