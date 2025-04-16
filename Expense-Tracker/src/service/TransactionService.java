package service;

import model.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionService {
    private final List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }

    public void updateTransaction(Transaction oldTransaction, Transaction newTransaction) {
        int index = transactions.indexOf(oldTransaction);
        if (index != -1) {
            transactions.set(index, newTransaction);
        }
    }

    public void clearTransactions() {
        transactions.clear();
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getTransactionsByType(String type) {
        return transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByCategory(String category) {
        return transactions.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByDate(LocalDate date) {
        return transactions.stream()
                .filter(t -> t.getDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<Transaction> getSortedByAmount(boolean ascending) {
        return transactions.stream()
                .sorted(ascending ?
                        Comparator.comparingDouble(Transaction::getAmount) :
                        Comparator.comparingDouble(Transaction::getAmount).reversed())
                .collect(Collectors.toList());
    }

    public Transaction getLargestTransaction() {
        return transactions.stream()
                .max(Comparator.comparingDouble(Transaction::getAmount))
                .orElse(null);
    }

    public Transaction getSmallestTransaction() {
        return transactions.stream()
                .min(Comparator.comparingDouble(Transaction::getAmount))
                .orElse(null);
    }

    public List<Transaction> searchByDescription(String keyword) {
        return transactions.stream()
                .filter(t -> t.getDescription() != null && t.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}
