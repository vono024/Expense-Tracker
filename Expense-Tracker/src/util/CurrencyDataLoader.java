package util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CurrencyDataLoader {
    public static Map<String, Double> loadRatesFromJson(String path) throws IOException {
        Gson gson = new Gson();
        FileReader reader = new FileReader(path);
        Type type = new TypeToken<HashMap<String, Double>>() {}.getType();
        return gson.fromJson(reader, type);
    }

    public static boolean isValidRateMap(Map<String, Double> map) {
        return map != null && !map.isEmpty() && map.values().stream().allMatch(r -> r > 0);
    }

    public static Map<String, Double> getDefaultRates() {
        Map<String, Double> defaultRates = new HashMap<>();
        defaultRates.put("UAH", 1.0);
        defaultRates.put("USD", 38.0);
        defaultRates.put("EUR", 41.0);
        return defaultRates;
    }
}
