package util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Currency;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CurrencyDataLoader {
    public static Map<String, Currency> loadCurrencies(String path) throws IOException {
        Gson gson = new Gson();
        FileReader reader = new FileReader(path);
        Type mapType = new TypeToken<HashMap<String, Currency>>() {}.getType();
        return gson.fromJson(reader, mapType);
    }
}
