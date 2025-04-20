package service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CurrencyService {

    private final java.util.Map<java.lang.String, java.lang.Double> rates = new java.util.HashMap<>();

    public CurrencyService() {
        rates.put("UAH", 1.0);
        fetchRatesFromInternet();
    }

    public java.util.Map<java.lang.String, java.lang.Double> getAllRates() {
        return new java.util.HashMap<>(rates);
    }

    public void updateRatesFromJson(java.util.Map<java.lang.String, java.lang.Double> newRates) {
        if (newRates != null && !newRates.isEmpty()) {
            rates.clear();
            rates.put("UAH", 1.0);
            for (java.util.Map.Entry<java.lang.String, java.lang.Double> entry : newRates.entrySet()) {
                if (entry.getValue() > 0) {
                    rates.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public double convert(double amount, java.lang.String fromCurrency, java.lang.String toCurrency) {
        if (!rates.containsKey(fromCurrency) || !rates.containsKey(toCurrency)) {
            return amount;
        }
        double baseAmount = amount * rates.get(fromCurrency);
        return baseAmount / rates.get(toCurrency);
    }

    public boolean isSupported(java.lang.String currencyCode) {
        return rates.containsKey(currencyCode);
    }

    public double getRate(java.lang.String currencyCode) {
        return rates.getOrDefault(currencyCode, 1.0);
    }

    public void addOrUpdateCurrency(java.lang.String code, double rate) {
        if (code != null && !code.isEmpty() && rate > 0) {
            rates.put(code, rate);
        }
    }

    public void removeCurrency(java.lang.String code) {
        if (!"UAH".equalsIgnoreCase(code)) {
            rates.remove(code);
        }
    }

    public void fetchRatesFromInternet() {
        try {
            java.net.URL url = new java.net.URL("https://open.er-api.com/v6/latest/USD");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
            java.lang.StringBuilder response = new java.lang.StringBuilder();
            java.lang.String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            com.google.gson.Gson gson = new com.google.gson.Gson();
            com.google.gson.JsonObject obj = gson.fromJson(response.toString(), com.google.gson.JsonObject.class);

            if (obj == null || !obj.has("rates")) {
                System.err.println("❗ API не повернув поле 'rates'. Повна відповідь: " + obj);
                return;
            }

            com.google.gson.JsonObject ratesJson = obj.getAsJsonObject("rates");

            java.util.Map<java.lang.String, java.lang.Double> updatedRates = new java.util.HashMap<>();
            updatedRates.put("USD", 1.0); // базова
            if (ratesJson.has("UAH")) {
                updatedRates.put("UAH", ratesJson.get("UAH").getAsDouble());
            }
            if (ratesJson.has("EUR")) {
                updatedRates.put("EUR", ratesJson.get("EUR").getAsDouble());
            }

            updateRatesFromJson(updatedRates);

            System.out.println("✅ Курси оновлено (USD-базово): " + updatedRates);

        } catch (java.lang.Exception e) {
            System.err.println("❌ Помилка при оновленні курсів валют:");
            e.printStackTrace();
        }
    }
}
