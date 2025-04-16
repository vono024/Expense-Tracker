package controller;

import model.Category;
import model.Transaction;
import service.CategoryService;
import service.CurrencyService;
import service.TransactionService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AddTransactionDialog extends JDialog {
    private final JTextField amountField = new JTextField();
    private final JTextField descriptionField = new JTextField();
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"income", "expense"});
    private final JComboBox<String> currencyBox = new JComboBox<>(new String[]{"UAH", "USD", "EUR"});
    private final JComboBox<String> categoryBox = new JComboBox<>();
    private final JButton saveButton = new JButton();

    private final TransactionService transactionService;
    private final Transaction existingTransaction;
    private final CategoryService categoryService;
    private final CurrencyService currencyService;

    public AddTransactionDialog(JFrame parent, TransactionService transactionService, CategoryService categoryService, CurrencyService currencyService) {
        this(parent, transactionService, null, categoryService, currencyService);
    }

    public AddTransactionDialog(JFrame parent, TransactionService transactionService, Transaction existing, CategoryService categoryService, CurrencyService currencyService) {
        super(parent, existing == null ? "Додати транзакцію" : "Редагувати транзакцію", true);
        this.transactionService = transactionService;
        this.existingTransaction = existing;
        this.categoryService = categoryService;
        this.currencyService = currencyService;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(7, 2));

        refreshCategories();

        if (existingTransaction != null) {
            amountField.setText(String.valueOf(existingTransaction.getAmount()));
            descriptionField.setText(existingTransaction.getDescription());
            typeBox.setSelectedItem(existingTransaction.getType());
            currencyBox.setSelectedItem(existingTransaction.getCurrency());
            categoryBox.setSelectedItem(existingTransaction.getCategory());
            saveButton.setText("Оновити");
        } else {
            saveButton.setText("Зберегти");
        }

        typeBox.addActionListener(e -> refreshCategories());

        add(new JLabel("Сума:"));
        add(amountField);
        add(new JLabel("Опис:"));
        add(descriptionField);
        add(new JLabel("Тип:"));
        add(typeBox);
        add(new JLabel("Категорія:"));
        add(categoryBox);
        add(new JLabel("Валюта:"));
        add(currencyBox);
        add(new JLabel(""));
        add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String desc = descriptionField.getText();
                String type = (String) typeBox.getSelectedItem();
                String category = (String) categoryBox.getSelectedItem();
                String currency = (String) currencyBox.getSelectedItem();
                LocalDate date = LocalDate.now();

                Transaction t = new Transaction(amount, category, date, desc, currency, type);

                if (existingTransaction != null) {
                    transactionService.updateTransaction(existingTransaction, t);
                } else {
                    transactionService.addTransaction(t);
                }
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Помилка введення.");
            }
        });
    }

    private void refreshCategories() {
        categoryBox.removeAllItems();
        String type = (String) typeBox.getSelectedItem();
        List<Category> filtered = categoryService.getAllCategories()
                .stream()
                .filter(c -> c.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
        for (Category c : filtered) {
            categoryBox.addItem(c.getName());
        }
    }
}
