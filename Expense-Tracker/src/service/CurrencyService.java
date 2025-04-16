package service;

import java.util.HashMap;
import java.util.Map;

public class CurrencyService {
    private Map<String, Double> rates = new HashMap<>();

    public CurrencyService() {
        rates.put("USD", 38.3);
        rates.put("EUR", 41.2);
        rates.put("UAH", 1.0);
    }

    public Map<String, Double> getAllRates() {
        return rates;
    }

    public void updateRatesFromJson(Map<String, Double> newRates) {
        rates.clear();
        rates.putAll(newRates);
    }

    public double convert(double amount, String from, String to) {
        if (!rates.containsKey(from) || !rates.containsKey(to)) return amount;
        double uahAmount = amount * rates.get(from);
        return uahAmount / rates.get(to);
    }
}
