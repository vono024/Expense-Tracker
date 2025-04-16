package model;

public class Currency {
    private String code;
    private double rateToBase;

    public Currency(String code, double rateToBase) {
        this.code = code;
        this.rateToBase = rateToBase;
    }

    public String getCode() {
        return code;
    }

    public double getRateToBase() {
        return rateToBase;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setRateToBase(double rateToBase) {
        this.rateToBase = rateToBase;
    }

    public double convert(double amount, Currency targetCurrency) {
        double amountInBase = amount * rateToBase;
        return amountInBase / targetCurrency.getRateToBase();
    }
}
