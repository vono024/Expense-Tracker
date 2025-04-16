package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Transaction;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class FileService {
    private final Gson gson = new Gson();

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

    private String escape(String text) {
        return text == null ? "" : text.replace(",", " ");
    }
}
