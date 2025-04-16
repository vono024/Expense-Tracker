package model;

import java.time.LocalDate;

public class Transaction {
    private double amount;
    private String category;
    private LocalDate date;
    private String description;
    private String currency;
    private String type;

    public Transaction(double amount, String category, LocalDate date, String description, String currency, String type) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
        this.currency = currency;
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getCurrency() {
        return currency;
    }

    public String getType() {
        return type;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setType(String type) {
        this.type = type;
    }
}
