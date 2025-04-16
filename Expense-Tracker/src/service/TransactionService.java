package service;

import model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    public void clearTransactions() {
        transactions.clear();
    }
}
