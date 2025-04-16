package service;

import model.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Transaction t : transactions) {
                writer.write(t.getAmount() + "," + t.getCategory() + "," + t.getDate() + "," +
                        t.getDescription() + "," + t.getCurrency() + "," + t.getType());
                writer.newLine();
            }
        }
    }

    public List<Transaction> loadFromCsv(String path) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Transaction t = new Transaction(
                        Double.parseDouble(parts[0]),
                        parts[1],
                        java.time.LocalDate.parse(parts[2]),
                        parts[3],
                        parts[4],
                        parts[5]
                );
                transactions.add(t);
            }
        }
        return transactions;
    }

    public void saveAsTxt(List<Transaction> transactions, String path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Transaction t : transactions) {
                writer.write(t.getType() + " | " + t.getDate() + " | " + t.getAmount() + " " +
                        t.getCurrency() + " | " + t.getCategory() + " | " + t.getDescription());
                writer.newLine();
            }
        }
    }
}
