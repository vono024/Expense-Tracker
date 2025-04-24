package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.Transaction;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileService {
    private final Gson gson;

    public FileService() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    public void saveAsJson(List<Transaction> transactions, String path) throws IOException {
        try (Writer writer = new FileWriter(path)) {
            gson.toJson(transactions, writer);
        }
    }

    public List<Transaction> loadFromJson(String path) throws IOException {
        try (Reader reader = new FileReader(path)) {
            Type listType = new TypeToken<List<Transaction>>() {}.getType();
            return gson.fromJson(reader, listType);
        }
    }

    public void saveAsCsv(List<Transaction> transactions, String path) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            writer.println("amount,category,date,description,currency,type");
            for (Transaction t : transactions) {
                writer.printf("%s,%s,%s,%s,%s,%s%n",
                        t.getAmount(),
                        escape(t.getCategory()),
                        t.getDate(),
                        escape(t.getDescription()),
                        t.getCurrency(),
                        t.getType());
            }
        }
    }

    public List<Transaction> loadFromCsv(String path) throws IOException {
        List<Transaction> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine(); // Пропускаємо заголовок
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 6) {
                    Transaction t = new Transaction();
                    t.setAmount(Double.parseDouble(parts[0]));
                    t.setCategory(parts[1]);
                    t.setDate(LocalDate.parse(parts[2]));
                    t.setDescription(parts[3]);
                    t.setCurrency(parts[4]);
                    t.setType(parts[5]);
                    list.add(t);
                }
            }
        }
        return list;
    }

    public void saveAsTxt(List<Transaction> transactions, String path) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            for (Transaction t : transactions) {
                writer.println("Сума: " + t.getAmount());
                writer.println("Категорія: " + t.getCategory());
                writer.println("Дата: " + t.getDate());
                writer.println("Опис: " + t.getDescription());
                writer.println("Валюта: " + t.getCurrency());
                writer.println("Тип: " + t.getType());
                writer.println("------------------------------");
            }
        }
    }

    public List<Transaction> loadFromTxt(String path) throws IOException {
        List<Transaction> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            Transaction t = new Transaction();
            String line;
            int counter = 0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Сума: ")) {
                    t.setAmount(Double.parseDouble(line.substring(6)));
                    counter++;
                } else if (line.startsWith("Категорія: ")) {
                    t.setCategory(line.substring(11));
                    counter++;
                } else if (line.startsWith("Дата: ")) {
                    t.setDate(LocalDate.parse(line.substring(6)));
                    counter++;
                } else if (line.startsWith("Опис: ")) {
                    t.setDescription(line.substring(6));
                    counter++;
                } else if (line.startsWith("Валюта: ")) {
                    t.setCurrency(line.substring(9));
                    counter++;
                } else if (line.startsWith("Тип: ")) {
                    t.setType(line.substring(5));
                    counter++;
                } else if (line.startsWith("------------------------------")) {
                    if (counter == 6) list.add(t);
                    t = new Transaction();
                    counter = 0;
                }
            }
        }
        return list;
    }

    private String escape(String text) {
        return text == null ? "" : text.replace(",", " ");
    }

    private static class LocalDateAdapter extends com.google.gson.TypeAdapter<LocalDate> {
        @Override
        public void write(JsonWriter writer, LocalDate localDate) throws IOException {
            writer.value(localDate.toString());
        }

        @Override
        public LocalDate read(JsonReader reader) throws IOException {
            return LocalDate.parse(reader.nextString());
        }
    }
}
