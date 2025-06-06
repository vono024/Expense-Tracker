package service;

import model.Transaction;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    private List<Transaction> transactions = new ArrayList<>();
    private final SimpleDatabase db = new SimpleDatabase();

    public TransactionService() {
        db.init();
        transactions = db.loadAll();
    }

    public void addTransaction(Transaction t) {
        transactions.add(t);
        db.save(t);
    }

    public void removeTransaction(Transaction t) {
        transactions.remove(t);
        db.clearAll();
        for (Transaction tx : transactions) {
            db.save(tx);
        }
    }

    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    public void clearTransactions() {
        transactions.clear();
        db.clearAll();
    }
}
