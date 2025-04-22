package model;

import java.util.Objects;

public class Currency {
    private String code;
    private String name;
    private double rateToUAH;

    public String getName() {
        return name;
    }

    public double getRateToUAH() {
        return rateToUAH;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRateToUAH(double rateToUAH) {
        this.rateToUAH = rateToUAH;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;
        Currency currency = (Currency) o;
        return code.equalsIgnoreCase(currency.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code.toUpperCase());
    }

    @Override
    public String toString() {
        return code + " (" + name + ")";
    }
}
