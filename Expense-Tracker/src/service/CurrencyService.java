package service;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import com.google.gson.*;

public class CurrencyService {

    private final Map<String, Double> rates = new HashMap<>();
    private final Map<String, Double> exchangeRateCache = new HashMap<>();

    public CurrencyService() {
        fetchRatesFromInternet();
    }

    public void fetchRatesFromInternet() {
        try {
            URL url = new URL("https://open.er-api.com/v6/latest/USD");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject rateData = json.getAsJsonObject("rates");

                if (rateData == null) {
                    System.err.println("API не повернув 'rates'. Використовую резервні значення.");
                    setFallbackRates();
                    return;
                }

                Double usdToUah = rateData.get("UAH").getAsDouble();
                Double eurToUsd = rateData.get("EUR").getAsDouble();

                rates.clear();
                exchangeRateCache.clear();
                rates.put("USD", usdToUah);
                rates.put("EUR", usdToUah / eurToUsd);
                rates.put("UAH", 1.0);

                System.out.println("Курси оновлено: " + rates);
            }
        } catch (Exception e) {
            System.err.println("Немає підключення до інтернету. Використовується останні або резервні курси.");
            setFallbackRates();
        }
    }

    private void setFallbackRates() {
        rates.clear();
        exchangeRateCache.clear();
        rates.put("USD", 39.00);
        rates.put("EUR", 42.30);
        rates.put("UAH", 1.0);
        System.out.println("Використано резервні курси: " + rates);
    }

    public double getRate(String currency) {
        return rates.getOrDefault(currency.toUpperCase(), 1.0);
    }

    public Map<String, Double> getRates() {
        return new HashMap<>(rates);
    }

    public double getExchangeRate(String from, String to) {
        String key = from.toUpperCase() + "_" + to.toUpperCase();
        if (exchangeRateCache.containsKey(key)) {
            System.out.println("Отримано з кешу: " + key);
            return exchangeRateCache.get(key);
        }

        if (!rates.containsKey(from.toUpperCase()) || !rates.containsKey(to.toUpperCase())) {
            throw new IllegalArgumentException("Невідомі валюти: " + from + " або " + to);
        }

        double rate = rates.get(from.toUpperCase()) / rates.get(to.toUpperCase());
        exchangeRateCache.put(key, rate);
        System.out.println("Обчислено та кешовано: " + key);
        return rate;
    }

    public void clearExchangeRateCache() {
        exchangeRateCache.clear();
        System.out.println("Кеш курсів очищено.");
    }
}
