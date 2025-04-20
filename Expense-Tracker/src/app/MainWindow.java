package app;

import controller.AddTransactionDialog;
import controller.StatsDialog;
import model.Transaction;
import model.Category;
import service.TransactionService;
import service.ReportService;
import service.FileService;
import service.CurrencyService;
import service.BudgetService;
import service.CategoryService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new java.awt.BorderLayout());

        initDefaultCategories();

        JPanel topPanel = new JPanel();

        JButton addBtn = new JButton("Додати");
        JButton statsBtn = new JButton("Статистика");
        JButton exportBtn = new JButton("Зберегти як");

        JPopupMenu exportMenu = new JPopupMenu();
        JMenuItem saveJson = new JMenuItem("JSON");
        JMenuItem saveCsv = new JMenuItem("CSV");
        JMenuItem saveTxt = new JMenuItem("TXT");
        exportMenu.add(saveJson);
        exportMenu.add(saveCsv);
        exportMenu.add(saveTxt);
        exportBtn.addActionListener(e -> exportMenu.show(exportBtn, 0, exportBtn.getHeight()));

        JButton loadBtn = new JButton("Завантажити");
        limitField = new JTextField(6);
        JButton limitBtn = new JButton("Ліміт");

        topPanel.add(addBtn);
        topPanel.add(statsBtn);
        topPanel.add(exportBtn);
        topPanel.add(loadBtn);
        topPanel.add(new JLabel("Ліміт:"));
        topPanel.add(limitField);
        topPanel.add(limitBtn);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel bottomPanel = new JPanel(new java.awt.GridLayout(2, 1));
        JPanel summary = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
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
        JPanel currencyPanel = new JPanel(new java.awt.BorderLayout());
        currencyPanel.setBorder(BorderFactory.createTitledBorder("Курси валют"));
        currencyPanel.add(currencyScroll, java.awt.BorderLayout.CENTER);

        bottomPanel.add(summary);
        bottomPanel.add(currencyPanel);

        add(topPanel, java.awt.BorderLayout.NORTH);
        add(scrollPane, java.awt.BorderLayout.CENTER);
        add(bottomPanel, java.awt.BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            AddTransactionDialog dialog = new AddTransactionDialog(this, transactionService, categoryService, currencyService);
            dialog.setVisible(true);
            updateTable();
        });

        statsBtn.addActionListener(e -> {
            StatsDialog dialog = new StatsDialog(this, transactionService);
            dialog.setVisible(true);
        });

        saveJson.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Зберегти як JSON");
            chooser.setFileFilter(new FileNameExtensionFilter("JSON файли (*.json)", "json"));
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    String path = chooser.getSelectedFile().getAbsolutePath();
                    if (!path.toLowerCase().endsWith(".json")) path += ".json";
                    fileService.saveAsJson(transactionService.getAllTransactions(), path);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Помилка при збереженні JSON.");
                }
            }
        });

        saveCsv.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Зберегти як CSV");
            chooser.setFileFilter(new FileNameExtensionFilter("CSV файли (*.csv)", "csv"));
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    String path = chooser.getSelectedFile().getAbsolutePath();
                    if (!path.toLowerCase().endsWith(".csv")) path += ".csv";
                    fileService.saveAsCsv(transactionService.getAllTransactions(), path);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Помилка при збереженні CSV.");
                }
            }
        });

        saveTxt.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Зберегти як TXT");
            chooser.setFileFilter(new FileNameExtensionFilter("Текстові файли (*.txt)", "txt"));
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    String path = chooser.getSelectedFile().getAbsolutePath();
                    if (!path.toLowerCase().endsWith(".txt")) path += ".txt";
                    fileService.saveAsTxt(transactionService.getAllTransactions(), path);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Помилка при збереженні TXT.");
                }
            }
        });

        loadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Виберіть JSON файл для завантаження");
            chooser.setFileFilter(new FileNameExtensionFilter("JSON файли (*.json)", "json"));
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    List<Transaction> list = fileService.loadFromJson(chooser.getSelectedFile().getAbsolutePath());
                    transactionService.clearTransactions();
                    list.forEach(transactionService::addTransaction);
                    updateTable();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Помилка при завантаженні файлу.");
                }
            }
        });

        limitBtn.addActionListener(e -> {
            try {
                double val = Double.parseDouble(limitField.getText());
                budgetService.setMonthlyLimit(val);
                updateTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Некоректне число");
            }
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

        new javax.swing.Timer(15000, e -> {
            currencyService.fetchRatesFromInternet();
            updateCurrency();
        }).start();
    }

    private void updateTable() {
        List<Transaction> list = transactionService.getAllTransactions();
        String[] cols = {"Сума", "Категорія", "Дата", "Опис", "Валюта", "Тип"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0);
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
        double usd = rates.getOrDefault("USD", 1.0);
        double eur = rates.getOrDefault("EUR", 1.0);
        sb.append("1 EUR → ").append(String.format("%.2f", eur)).append(" UAH\n");
        sb.append("1 USD → ").append(String.format("%.2f", usd)).append(" UAH\n");
        currencyArea.setText(sb.toString());
    }

    private void initDefaultCategories() {
        categoryService.addCategory(new Category("Зарплата", "income", "💰"));
        categoryService.addCategory(new Category("Фріланс", "income", "🧑‍"));
        categoryService.addCategory(new Category("Подарунок", "income", "🎁"));
        categoryService.addCategory(new Category("Їжа", "expense", "🍔"));
        categoryService.addCategory(new Category("Транспорт", "expense", "🚗"));
        categoryService.addCategory(new Category("Розваги", "expense", "🎮"));
        categoryService.addCategory(new Category("Медицина", "expense", "💊"));
    }
}
