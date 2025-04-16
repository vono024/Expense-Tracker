package service;

import model.Currency;

import java.util.HashMap;
import java.util.Map;

public class CurrencyService {
    private Map<String, Currency> currencies = new HashMap<>();
    private String baseCurrency = "UAH";

    public void addCurrency(Currency currency) {
        currencies.put(currency.getCode(), currency);
    }

    public Currency getCurrency(String code) {
        return currencies.get(code);
    }

    public double convert(double amount, String fromCode, String toCode) {
        Currency from = currencies.get(fromCode);
        Currency to = currencies.get(toCode);
        if (from != null && to != null) {
            return from.convert(amount, to);
        }
        return amount;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public Map<String, Currency> getAllCurrencies() {
        return currencies;
    }

    public void clear() {
        currencies.clear();
    }
}
