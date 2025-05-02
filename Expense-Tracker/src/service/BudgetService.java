package service;

import java.io.*;
import java.sql.*;

public class BudgetService {
    private double monthlyLimit = 0.0;
    private static final String FILE_PATH = "resources/limits-budget.txt";
    private static final String DB_URL = "jdbc:sqlite:expense_tracker.db";

    public BudgetService() {
        createTableIfNotExists();
        load();
    }

    public void setMonthlyLimit(double limit) {
        this.monthlyLimit = limit;
        saveToFile();
        saveToDatabase(limit);
    }

    public double getMonthlyLimit() {
        double dbLimit = loadFromDatabase();
        if (dbLimit > 0) {
            monthlyLimit = dbLimit;
        }
        return monthlyLimit;
    }

    public boolean isLimitExceeded(double totalExpense) {
        return totalExpense > getMonthlyLimit() && monthlyLimit > 0;
    }

    public void clear() {
        monthlyLimit = 0.0;
        saveToFile();
        clearDatabase();
    }

    private void saveToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH))) {
            out.println(monthlyLimit);
        } catch (IOException e) {
            System.out.println("Помилка збереження глобального ліміту у файл: " + e.getMessage());
        }
    }

    private void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) {
                monthlyLimit = Double.parseDouble(line);
            }
        } catch (IOException e) {
            System.out.println("Помилка завантаження глобального ліміту з файлу: " + e.getMessage());
        }
    }

    private void saveToDatabase(double limit) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("REPLACE INTO budget_limit (id, amount) VALUES (1, ?)")) {
            stmt.setDouble(1, limit);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Помилка збереження глобального ліміту в БД: " + e.getMessage());
        }
    }

    private double loadFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT amount FROM budget_limit WHERE id = 1")) {
            if (rs.next()) {
                return rs.getDouble("amount");
            }
        } catch (SQLException e) {
            System.out.println("Помилка завантаження глобального ліміту з БД: " + e.getMessage());
        }
        return 0.0;
    }

    private void clearDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM budget_limit");
        } catch (SQLException e) {
            System.out.println("Помилка очищення глобального ліміту з БД: " + e.getMessage());
        }
    }

    private void createTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS budget_limit (id INTEGER PRIMARY KEY, amount REAL)");
        } catch (SQLException e) {
            System.out.println("Помилка створення таблиці ліміту в БД: " + e.getMessage());
        }
    }
}
