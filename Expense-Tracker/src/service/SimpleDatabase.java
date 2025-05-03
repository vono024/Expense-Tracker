package service;

import model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SimpleDatabase {
    private final Connection conn = DatabaseConnection.getInstance().getConnection();

    public void save(Transaction t) {
        String sql = "INSERT INTO transactions (amount, category, date, description, currency, type) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, t.getAmount());
            ps.setString(2, t.getCategory());
            ps.setString(3, t.getDate().toString());
            ps.setString(4, t.getDescription());
            ps.setString(5, t.getCurrency());
            ps.setString(6, t.getType());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> loadAll() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        LocalDate.parse(rs.getString("date")),
                        rs.getString("description"),
                        rs.getString("currency"),
                        rs.getString("type")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void clearAll() {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM transactions");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                amount REAL,
                category TEXT,
                date TEXT,
                description TEXT,
                currency TEXT,
                type TEXT
            )
        """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
