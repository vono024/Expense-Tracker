package controller;

import model.Category;
import model.Transaction;
import service.CategoryService;
import service.CurrencyService;
import service.TransactionService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        updateCategories(categoryService);

        typeBox.addActionListener(e -> updateCategories(categoryService));

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

                Transaction t = new Transaction(amountInUAH, category, date, description, currency, type);
                transactionService.addTransaction(t);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введіть коректну суму.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Помилка збереження транзакції.");
            }
        });
    }

    private void updateCategories(CategoryService categoryService) {
        String selectedType = (String) typeBox.getSelectedItem();
        List<Category> filtered = categoryService.getAllCategories().stream()
                .filter(c -> c.getType().equals(selectedType))
                .collect(Collectors.toList());

        categoryBox.removeAllItems();
        for (Category c : filtered) {
            categoryBox.addItem(c.getName());
        }
    }

    public AddTransactionDialog(JFrame parent, TransactionService transactionService, Transaction transaction, CategoryService categoryService, CurrencyService currencyService) {
        this(parent, transactionService, categoryService, currencyService);

        amountField.setText(String.valueOf(transaction.getAmount()));
        descriptionField.setText(transaction.getDescription());
        typeBox.setSelectedItem(transaction.getType());
        currencyBox.setSelectedItem(transaction.getCurrency());
        categoryBox.setSelectedItem(transaction.getCategory());

        for (ActionListener al : saveBtn.getActionListeners()) {
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
                JOptionPane.showMessageDialog(this, "Введіть коректну суму.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Помилка оновлення транзакції.");
            }
        });
    }
}
