package controller;

import model.Category;
import model.Transaction;
import service.BudgetService;
import service.CategoryLimitService;
import service.CategoryService;
import service.CurrencyService;
import service.TimeLimitService;
import service.TransactionService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AddTransactionDialog extends JDialog {
    private final JTextField amountField = new JTextField(10);
    private final JTextField descriptionField = new JTextField(10);
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"income", "expense"});
    private final JComboBox<String> currencyBox = new JComboBox<>(new String[]{"UAH", "USD", "EUR"});
    private final JComboBox<String> categoryBox = new JComboBox<>();
    private final JButton saveBtn = new JButton("Зберегти");

    private final BudgetService budgetService = new BudgetService();
    private final CategoryLimitService categoryLimitService = new CategoryLimitService();
    private final TimeLimitService timeLimitService = new TimeLimitService();

    public AddTransactionDialog(JFrame parent, TransactionService transactionService, CategoryService categoryService, CurrencyService currencyService) {
        super(parent, "Додати транзакцію", true);
        setSize(300, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(6, 2, 5, 5));

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
        add(new JLabel());
        add(saveBtn);

        typeBox.addActionListener(e -> updateCategories(categoryService));
        updateCategories(categoryService);

        for (String currency : currencyService.getAllRates().keySet()) {
            currencyBox.addItem(currency);
        }

        amountField.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                if (str == null) return;
                String newText = getText(0, getLength()) + str;
                if (newText.matches("^\\d*(\\.\\d{0,2})?$")) {
                    super.insertString(offs, str, a);
                }
            }
        });

        saveBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) throw new NumberFormatException();

                String description = descriptionField.getText();
                String type = (String) typeBox.getSelectedItem();
                String category = (String) categoryBox.getSelectedItem();
                String currency = (String) currencyBox.getSelectedItem();
                LocalDate date = LocalDate.now();

                double rate = currencyService.getRate(currency);
                double amountInUAH = amount * rate;

                Transaction newTransaction = new Transaction(amountInUAH, category, date, description, currency, type);

                if (type.equals("expense")) {
                    List<Transaction> all = transactionService.getAllTransactions();

                    if (budgetService.isLimitExceeded(reportTotal(all) + amountInUAH)) {
                        showWarning("Перевищено глобальний ліміт місяця!");
                        return;
                    }

                    if (categoryLimitService.isLimitExceeded(category, all)) {
                        showWarning("Перевищено ліміт по категорії: " + category);
                        return;
                    }

                    if (timeLimitService.isLimitExceeded(TimeLimitService.LimitType.DAILY, all)) {
                        showWarning("Перевищено денний ліміт!");
                        return;
                    }

                    if (timeLimitService.isLimitExceeded(TimeLimitService.LimitType.WEEKLY, all)) {
                        showWarning("Перевищено тижневий ліміт!");
                        return;
                    }
                }

                transactionService.addTransaction(newTransaction);
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Некоректна сума.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Помилка збереження.");
            }
        });
    }

    private void updateCategories(CategoryService categoryService) {
        categoryBox.removeAllItems();
        String type = (String) typeBox.getSelectedItem();
        List<String> names = categoryService.getAllCategories().stream()
                .filter(c -> c.getType().equals(type))
                .map(Category::getName)
                .collect(Collectors.toList());
        for (String c : names) categoryBox.addItem(c);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Увага", JOptionPane.WARNING_MESSAGE);
    }

    private double reportTotal(List<Transaction> list) {
        return list.stream()
                .filter(t -> t.getType().equals("expense"))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public AddTransactionDialog(JFrame parent, TransactionService transactionService, Transaction transaction, CategoryService categoryService, CurrencyService currencyService) {
        this(parent, transactionService, categoryService, currencyService);

        amountField.setText(String.valueOf(transaction.getAmount()));
        descriptionField.setText(transaction.getDescription());
        typeBox.setSelectedItem(transaction.getType());
        currencyBox.setSelectedItem(transaction.getCurrency());
        categoryBox.setSelectedItem(transaction.getCategory());

        for (java.awt.event.ActionListener al : saveBtn.getActionListeners()) {
            saveBtn.removeActionListener(al);
        }

        saveBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) throw new NumberFormatException();

                String description = descriptionField.getText();
                String type = (String) typeBox.getSelectedItem();
                String category = (String) categoryBox.getSelectedItem();
                String currency = (String) currencyBox.getSelectedItem();
                LocalDate date = transaction.getDate();

                double rate = currencyService.getRate(currency);
                double amountInUAH = amount * rate;

                Transaction updated = new Transaction(amountInUAH, category, date, description, currency, type);
                transactionService.updateTransaction(transaction, updated);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Некоректна сума.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Помилка оновлення.");
            }
        });
    }
}
