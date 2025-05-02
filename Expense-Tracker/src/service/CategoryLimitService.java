package service;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CategoryLimitService {

    private Map<String, Double> categoryLimits = new HashMap<>();
    private static final String DB_URL = "jdbc:sqlite:expense_tracker.db";

    public CategoryLimitService() {
        createTableIfNotExists();
        loadFromDatabase();
    }

    public void setLimit(String category, double limit) {
        categoryLimits.put(category, limit);
        saveToDatabase(category, limit);
    }

    public double getLimit(String category) {
        return categoryLimits.getOrDefault(category, 0.0);
    }

    public Map<String, Double> getAllLimits() {
        return categoryLimits;
    }

    public boolean isLimitExceeded(String category, double amount) {
        double currentLimit = getLimit(category);
        return currentLimit >= 0 && (amount > currentLimit);
    }

    public void clear() {
        categoryLimits.clear();
        clearDatabase();
    }

    private void saveToDatabase(String category, double limit) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "REPLACE INTO category_limits (category, amount) VALUES (?, ?)")) {
            stmt.setString(1, category);
            stmt.setDouble(2, limit);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Помилка збереження ліміту категорії в БД: " + e.getMessage());
        }
    }

    private void loadFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT category, amount FROM category_limits")) {
            while (rs.next()) {
                categoryLimits.put(rs.getString("category"), rs.getDouble("amount"));
            }
        } catch (SQLException e) {
            System.out.println("Помилка завантаження лімітів категорій з БД: " + e.getMessage());
        }
    }

    private void clearDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM category_limits");
        } catch (SQLException e) {
            System.out.println("Помилка очищення таблиці category_limits: " + e.getMessage());
        }
    }

    private void createTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS category_limits (" +
                    "category TEXT PRIMARY KEY, " +
                    "amount REAL)");
        } catch (SQLException e) {
            System.out.println("Помилка створення таблиці category_limits: " + e.getMessage());
        }
    }
}
