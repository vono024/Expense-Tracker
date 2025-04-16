package controller;

import model.Transaction;
import service.TransactionService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AddTransactionDialog extends JDialog {
    private JTextField amountField;
    private JTextField descriptionField;
    private JComboBox<String> categoryBox;
    private JComboBox<String> currencyBox;
    private JComboBox<String> typeBox;
    private JButton saveButton;

    private TransactionService transactionService;
    private Transaction existing;

    public AddTransactionDialog(JFrame parent, TransactionService service) {
        this(parent, service, null);
    }

    public AddTransactionDialog(JFrame parent, TransactionService service, Transaction existingTransaction) {
        super(parent, existingTransaction == null ? "Додати транзакцію" : "Редагувати транзакцію", true);
        this.transactionService = service;
        this.existing = existingTransaction;

        setSize(400, 300);
        setLayout(new GridLayout(7, 2));
        setLocationRelativeTo(parent);

        amountField = new JTextField();
        descriptionField = new JTextField();
        categoryBox = new JComboBox<>(new String[]{"Їжа", "Транспорт", "Розваги", "Інше"});
        currencyBox = new JComboBox<>(new String[]{"UAH", "USD", "EUR"});
        typeBox = new JComboBox<>(new String[]{"income", "expense"});
        saveButton = new JButton(existing == null ? "Зберегти" : "Оновити");

        if (existing != null) {
            amountField.setText(String.valueOf(existing.getAmount()));
            descriptionField.setText(existing.getDescription());
            categoryBox.setSelectedItem(existing.getCategory());
            currencyBox.setSelectedItem(existing.getCurrency());
            typeBox.setSelectedItem(existing.getType());
        }

        add(new JLabel("Сума:")); add(amountField);
        add(new JLabel("Опис:")); add(descriptionField);
        add(new JLabel("Категорія:")); add(categoryBox);
        add(new JLabel("Валюта:")); add(currencyBox);
        add(new JLabel("Тип:")); add(typeBox);
        add(new JLabel()); add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText();
                String category = (String) categoryBox.getSelectedItem();
                String currency = (String) currencyBox.getSelectedItem();
                String type = (String) typeBox.getSelectedItem();
                LocalDate date = LocalDate.now();

                Transaction transaction = new Transaction(amount, category, date, description, currency, type);

                if (existing != null) {
                    service.removeTransaction(existing);
                }
                service.addTransaction(transaction);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Невірні дані");
            }
        });
    }
}
