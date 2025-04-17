package app;

import controller.AddTransactionDialog;
import controller.StatsDialog;
import model.Transaction;
import model.Category;
import service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.io.IOException;

public class MainWindow extends JFrame {
    private JTable table;
    private JLabel incomeLabel, expenseLabel, balanceLabel, limitLabel;
    private JTextField limitField;
    private JTextArea currencyArea;

    private final TransactionService transactionService = new TransactionService();
    private final ReportService reportService = new ReportService();
    private final FileService fileService = new FileService();
    private final CurrencyService currencyService = new CurrencyService();
    private final BudgetService budgetService = new BudgetService();
    private final CategoryService categoryService = new CategoryService();

    public MainWindow() {
        setTitle("Фінансовий трекер");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initDefaultCategories();

        JPanel topPanel = new JPanel();

        JButton addBtn = new JButton("Додати");
        JButton statsBtn = new JButton("Статистика");
        JButton saveJson = new JButton("Зберегти JSON");
        JButton saveCsv = new JButton("Зберегти CSV");
        JButton saveTxt = new JButton("Зберегти TXT");
        JButton loadBtn = new JButton("Завантажити");
        JButton updateBtn = new JButton("Оновити");
        JButton refreshRatesBtn = new JButton("Оновити курси");

        limitField = new JTextField(6);
        JButton limitBtn = new JButton("Ліміт");

        topPanel.add(addBtn);
        topPanel.add(statsBtn);
        topPanel.add(saveJson);
        topPanel.add(saveCsv);
        topPanel.add(saveTxt);
        topPanel.add(loadBtn);
        topPanel.add(updateBtn);
        topPanel.add(refreshRatesBtn);
        topPanel.add(new JLabel("Ліміт:"));
        topPanel.add(limitField);
        topPanel.add(limitBtn);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        JPanel summary = new JPanel(new FlowLayout(FlowLayout.LEFT));
        incomeLabel = new JLabel();
        expenseLabel = new JLabel();
        balanceLabel = new JLabel();
        limitLabel = new JLabel();

        summary.add(incomeLabel);
        summary.add(expenseLabel);
        summary.add(balanceLabel);
        summary.add(limitLabel);

        currencyArea = new JTextArea(4, 30);
        currencyArea.setEditable(false);
        JScrollPane currencyScroll = new JScrollPane(currencyArea);
        JPanel currencyPanel = new JPanel(new BorderLayout());
        currencyPanel.setBorder(BorderFactory.createTitledBorder("Курси валют"));
        currencyPanel.add(currencyScroll, BorderLayout.CENTER);

        bottomPanel.add(summary);
        bottomPanel.add(currencyPanel);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            AddTransactionDialog dialog = new AddTransactionDialog(this, transactionService, categoryService, currencyService);
            dialog.setVisible(true);
            updateTable();
        });

        statsBtn.addActionListener(e -> {
            StatsDialog dialog = new StatsDialog(this, transactionService);
            dialog.setVisible(true);
        });

        updateBtn.addActionListener(e -> updateTable());

        saveJson.addActionListener(e -> {
            try {
                fileService.saveAsJson(transactionService.getAllTransactions(), "resources/transactions.json");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Помилка при збереженні JSON.");
            }
        });

        saveCsv.addActionListener(e -> {
            try {
                fileService.saveAsCsv(transactionService.getAllTransactions(), "resources/transactions.csv");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Помилка при збереженні CSV.");
            }
        });

        saveTxt.addActionListener(e -> {
            try {
                fileService.saveAsTxt(transactionService.getAllTransactions(), "resources/transactions.txt");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Помилка при збереженні TXT.");
            }
        });

        loadBtn.addActionListener(e -> {
            try {
                List<Transaction> list = fileService.loadFromJson("resources/transactions.json");
                transactionService.clearTransactions();
                list.forEach(transactionService::addTransaction);
                updateTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Помилка при завантаженні.");
            }
        });

        limitBtn.addActionListener(e -> {
            try {
                double val = Double.parseDouble(limitField.getText());
                budgetService.setMonthlyLimit(val);
                updateSummary();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Некоректне число");
            }
        });

        refreshRatesBtn.addActionListener(e -> {
            currencyService.fetchRatesFromInternet();
            updateCurrency();
            JOptionPane.showMessageDialog(this, "Курси валют оновлено з інтернету.");
        });

        JPopupMenu popup = new JPopupMenu();
        JMenuItem edit = new JMenuItem("Редагувати");
        JMenuItem delete = new JMenuItem("Видалити");
        popup.add(edit);
        popup.add(delete);
        table.setComponentPopupMenu(popup);

        edit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                Transaction t = transactionService.getAllTransactions().get(row);
                AddTransactionDialog dialog = new AddTransactionDialog(this, transactionService, t, categoryService, currencyService);
                dialog.setVisible(true);
                updateTable();
            }
        });

        delete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                Transaction t = transactionService.getAllTransactions().get(row);
                int confirm = JOptionPane.showConfirmDialog(this, "Видалити транзакцію?", "Підтвердження", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    transactionService.removeTransaction(t);
                    updateTable();
                }
            }
        });

        updateTable();
    }

    private void updateTable() {
        List<Transaction> list = transactionService.getAllTransactions();
        String[] cols = {"Сума", "Категорія", "Дата", "Опис", "Валюта", "Тип"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Transaction t : list) {
            model.addRow(new Object[]{
                    t.getAmount(),
                    t.getCategory(),
                    t.getDate(),
                    t.getDescription(),
                    t.getCurrency(),
                    t.getType()
            });
        }
        table.setModel(model);
        updateSummary();
        updateCurrency();
    }

    private void updateSummary() {
        List<Transaction> list = transactionService.getAllTransactions();
        double income = reportService.getTotalByType(list, "income");
        double expense = reportService.getTotalByType(list, "expense");
        double balance = income - expense;
        double limit = budgetService.getMonthlyLimit();
        boolean exceeded = budgetService.isLimitExceeded(expense);

        incomeLabel.setText(" Дохід: " + income + " грн ");
        expenseLabel.setText(" Витрати: " + expense + " грн ");
        balanceLabel.setText(" Баланс: " + balance + " грн ");
        limitLabel.setText(" Ліміт: " + limit + " грн " + (exceeded ? "(перевищено!)" : ""));
    }

    private void updateCurrency() {
        Map<String, Double> rates = currencyService.getAllRates();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : rates.entrySet()) {
            sb.append(entry.getKey()).append(" → UAH: ").append(entry.getValue()).append("\n");
        }
        currencyArea.setText(sb.toString());
    }

    private void initDefaultCategories() {
        categoryService.addCategory(new Category("Зарплата", "income", "💰"));
        categoryService.addCategory(new Category("Фріланс", "income", "🧑‍💻"));
        categoryService.addCategory(new Category("Подарунок", "income", "🎁"));
        categoryService.addCategory(new Category("Їжа", "expense", "🍔"));
        categoryService.addCategory(new Category("Транспорт", "expense", "🚗"));
        categoryService.addCategory(new Category("Розваги", "expense", "🎮"));
        categoryService.addCategory(new Category("Медицина", "expense", "💊"));
    }
}
