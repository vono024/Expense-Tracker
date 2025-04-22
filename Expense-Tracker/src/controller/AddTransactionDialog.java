package controller;

import model.Transaction;
import model.Category;
import service.CategoryService;
import service.CurrencyService;
import service.TransactionService;
import service.BudgetService;
import service.CategoryLimitService;
import service.TimeLimitService;
import service.ReportService;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.time.LocalDate;

public class AddTransactionDialog extends JDialog {
    private final JTextField amountField = new JTextField();
    private final JTextField descriptionField = new JTextField();
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"income", "expense"});
    private final JComboBox<String> categoryBox = new JComboBox<>();
    private final JComboBox<String> currencyBox = new JComboBox<>();
    private final JButton saveBtn = new JButton("Зберегти");

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final CurrencyService currencyService;
    private Transaction existing;

    public AddTransactionDialog(JFrame parent,
                                TransactionService transactionService,
                                CategoryService categoryService,
                                CurrencyService currencyService) {
        super(parent, "Додати транзакцію", true);
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.currencyService = currencyService;

        initUI();
    }

    public AddTransactionDialog(JFrame parent,
                                TransactionService transactionService,
                                Transaction existing,
                                CategoryService categoryService,
                                CurrencyService currencyService) {
        this(parent, transactionService, categoryService, currencyService);
        this.existing = existing;
        fillForm(existing);
    }

    private void initUI() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setLayout(new GridLayout(6, 2, 10, 10));

        updateCategoryComboBox();

        add(new JLabel("Сума:")); add(amountField);
        add(new JLabel("Опис:")); add(descriptionField);
        add(new JLabel("Тип:")); add(typeBox);
        add(new JLabel("Категорія:")); add(categoryBox);
        add(new JLabel("Валюта:")); add(currencyBox);
        add(new JLabel()); add(saveBtn);

        saveBtn.addActionListener(e -> saveTransaction());

        setDecimalInputFilter(amountField);

        typeBox.addActionListener(e -> updateCategoryComboBox());

        updateCurrencyComboBox();
    }

    private void fillForm(Transaction t) {
        amountField.setText(String.valueOf(t.getAmount()));
        descriptionField.setText(t.getDescription());
        typeBox.setSelectedItem(t.getType());
        categoryBox.setSelectedItem(t.getCategory());
        currencyBox.setSelectedItem(t.getCurrency());
    }

    private void saveTransaction() {
        try {
            double amount = Double.parseDouble(amountField.getText().replace(",", "."));
            String description = descriptionField.getText();
            String type = (String) typeBox.getSelectedItem();
            String category = (String) categoryBox.getSelectedItem();
            String currency = (String) currencyBox.getSelectedItem();
            LocalDate date = LocalDate.now();

            double rate = currencyService.getRate(currency);
            if (rate <= 0) {
                showWarning("Неможливо отримати курс для валюти: " + currency);
                return;
            }

            double amountInUAH = amount * rate;

            Transaction t = new Transaction(amountInUAH, category, date, description, "UAH", type);
            if (existing != null) {
                transactionService.removeTransaction(existing);
            }
            transactionService.addTransaction(t);
            dispose();
        } catch (NumberFormatException ex) {
            showWarning("Некоректна сума");
        }
    }


    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Перевищення ліміту", JOptionPane.WARNING_MESSAGE);
    }

    private void updateCategoryComboBox() {
        String selectedType = (String) typeBox.getSelectedItem();

        categoryBox.removeAllItems();

        if ("income".equals(selectedType)) {
            for (Category c : categoryService.getAllCategories()) {
                if (c.getType().equals("income")) {
                    categoryBox.addItem(c.getName());
                }
            }
        } else if ("expense".equals(selectedType)) {
            for (Category c : categoryService.getAllCategories()) {
                if (c.getType().equals("expense")) {
                    categoryBox.addItem(c.getName());
                }
            }
        }
    }

    private void updateCurrencyComboBox() {
        currencyBox.removeAllItems();
        for (String code : currencyService.getAllRates().keySet()) {
            currencyBox.addItem(code);
        }
    }

    private void setDecimalInputFilter(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                String text = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = text.substring(0, offset) + string + text.substring(offset);
                if (isValidInput(newText)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (isValidInput(newText)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean isValidInput(String text) {
                return text.matches("\\d{0,10}(\\.\\d{0,2})?");
            }
        });
    }
}