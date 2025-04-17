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

    public void fetchRatesFromInternet() {
        try {
            URL url = new URL("https://api.exchangerate.host/latest?base=UAH");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            Gson gson = new Gson();
            JsonObject obj = gson.fromJson(response.toString(), JsonObject.class);
            JsonObject ratesJson = obj.getAsJsonObject("rates");

            Map<String, Double> updatedRates = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : ratesJson.entrySet()) {
                updatedRates.put(entry.getKey(), entry.getValue().getAsDouble());
            }

            updateRatesFromJson(updatedRates);
        } catch (Exception e) {
            System.err.println("Помилка при оновленні курсів валют: " + e.getMessage());
        }
    }
}
