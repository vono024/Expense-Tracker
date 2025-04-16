package service;

import java.util.HashMap;
import java.util.Map;

public class CurrencyService {
    private final Map<String, Double> rates = new HashMap<>();

    public CurrencyService() {
        rates.put("UAH", 1.0);
        rates.put("USD", 38.0);
        rates.put("EUR", 41.0);
    }

    public Map<String, Double> getAllRates() {
        return new HashMap<>(rates);
    }

    public void updateRatesFromJson(Map<String, Double> newRates) {
        if (newRates != null && !newRates.isEmpty()) {
            rates.clear();
            for (Map.Entry<String, Double> entry : newRates.entrySet()) {
                if (entry.getValue() > 0) {
                    rates.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public double convert(double amount, String fromCurrency, String toCurrency) {
        if (!rates.containsKey(fromCurrency) || !rates.containsKey(toCurrency)) {
            return amount;
        }
        double baseAmount = amount * rates.get(fromCurrency);
        return baseAmount / rates.get(toCurrency);
    }

    public boolean isSupported(String currencyCode) {
        return rates.containsKey(currencyCode);
    }

    public double getRate(String currencyCode) {
        return rates.getOrDefault(currencyCode, 1.0);
    }

    public void addOrUpdateCurrency(String code, double rate) {
        if (code != null && !code.isEmpty() && rate > 0) {
            rates.put(code, rate);
        }
    }

    public void removeCurrency(String code) {
        if (!"UAH".equalsIgnoreCase(code)) {
            rates.remove(code);
        }
    }
}
