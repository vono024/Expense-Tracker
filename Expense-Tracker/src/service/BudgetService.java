package service;

import java.io.*;

public class BudgetService {
    private double monthlyLimit = 0.0;
    private static final String FILE_PATH = "resources/limits-budget.txt";

    public BudgetService() {
        load();
    }

    public void setMonthlyLimit(double limit) {
        this.monthlyLimit = limit;
        save();
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }

    public boolean isLimitExceeded(double totalExpense) {
        return totalExpense > monthlyLimit && monthlyLimit > 0;
    }

    public void clear() {
        monthlyLimit = 0.0;
        save();
    }

    private void save() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH))) {
            out.println(monthlyLimit);
        } catch (IOException e) {
            System.out.println("Помилка збереження глобального ліміту: " + e.getMessage());
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
            System.out.println("Помилка завантаження глобального ліміту: " + e.getMessage());
        }
    }
}