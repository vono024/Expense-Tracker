package service;

import java.sql.*;

public class TimeLimitService {

    public enum LimitType {DAILY, WEEKLY}

    private double dailyLimit;
    private double weeklyLimit;

    private static final String DB_URL = "jdbc:sqlite:expense_tracker.db";

    public TimeLimitService() {
        createTableIfNotExists();
        loadFromDatabase();
    }

    public void setLimit(LimitType type, double limit) {
        if (type == LimitType.DAILY) {
            this.dailyLimit = limit;
        } else if (type == LimitType.WEEKLY) {
            this.weeklyLimit = limit;
        }
        saveToDatabase(type, limit);
    }

    public double getLimit(LimitType type) {
        return switch (type) {
            case DAILY -> dailyLimit;
            case WEEKLY -> weeklyLimit;
        };
    }

    public boolean isLimitExceeded(LimitType type, double totalExpenseForPeriod) {
        return switch (type) {
            case DAILY -> totalExpenseForPeriod > dailyLimit;
            case WEEKLY -> totalExpenseForPeriod > weeklyLimit;
        };
    }

    public void clear() {
        dailyLimit = 0;
        weeklyLimit = 0;
        clearDatabase();
    }

    private void saveToDatabase(LimitType type, double limit) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "REPLACE INTO time_limits (type, amount) VALUES (?, ?)")) {
            stmt.setString(1, type.name());
            stmt.setDouble(2, limit);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Помилка збереження часового ліміту в БД: " + e.getMessage());
        }
    }

    private void loadFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT type, amount FROM time_limits")) {
            while (rs.next()) {
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                if ("DAILY".equals(type)) {
                    dailyLimit = amount;
                } else if ("WEEKLY".equals(type)) {
                    weeklyLimit = amount;
                }
            }
        } catch (SQLException e) {
            System.out.println("Помилка завантаження часового ліміту з БД: " + e.getMessage());
        }
    }

    private void clearDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM time_limits");
        } catch (SQLException e) {
            System.out.println("Помилка очищення таблиці time_limits: " + e.getMessage());
        }
    }

    private void createTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS time_limits (" +
                    "type TEXT PRIMARY KEY, " +
                    "amount REAL)");
        } catch (SQLException e) {
            System.out.println("Помилка створення таблиці time_limits: " + e.getMessage());
        }
    }
}
