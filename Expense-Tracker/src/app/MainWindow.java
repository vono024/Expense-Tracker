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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        setLayout(new BorderLayout());

        initDefaultCategories();

        JPanel topPanel = new JPanel();

        JButton addBtn = new JButton("Додати");
        JButton statsBtn = new JButton("Статистика");
        JButton saveBtn = new JButton("Зберегти як");
        JButton loadBtn = new JButton("Завантажити");

        limitField = new JTextField(6);
        JButton limitBtn = new JButton("Ліміт");

        topPanel.add(addBtn);
        topPanel.add(statsBtn);
        topPanel.add(saveBtn);
        topPanel.add(loadBtn);
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

        saveBtn.addActionListener(e -> {
            String[] formats = {"JSON", "CSV", "TXT"};
            String selected = (String) JOptionPane.showInputDialog(this, "Формат:", "Зберегти як", JOptionPane.PLAIN_MESSAGE, null, formats, formats[0]);
            if (selected != null) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Зберегти як " + selected);
                int result = chooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        String path = chooser.getSelectedFile().getAbsolutePath();
                        switch (selected) {
                            case "JSON" -> fileService.saveAsJson(transactionService.getAllTransactions(), path);
                            case "CSV" -> fileService.saveAsCsv(transactionService.getAllTransactions(), path);
                            case "TXT" -> fileService.saveAsTxt(transactionService.getAllTransactions(), path);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Помилка при збереженні.");
                    }
                }
            }
        });

        loadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Завантажити JSON");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    List<Transaction> list = fileService.loadFromJson(chooser.getSelectedFile().getAbsolutePath());
                    transactionService.clearTransactions();
                    list.forEach(transactionService::addTransaction);
                    updateTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Помилка при завантаженні.");
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
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Transaction t : list) {
            model.addRow(new Object[]{
                    String.format("%.2f", t.getAmount()),
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

        incomeLabel.setText(" Дохід: " + String.format("%.2f", income) + " грн ");
        expenseLabel.setText(" Витрати: " + String.format("%.2f", expense) + " грн ");
        balanceLabel.setText(" Баланс: " + String.format("%.2f", balance) + " грн ");
        limitLabel.setText(" Ліміт: " + String.format("%.2f", limit) + " грн " + (exceeded ? "(перевищено!)" : ""));
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
        categoryService.addCategory(new Category("Фріланс", "income", "🧑‍💻"));
        categoryService.addCategory(new Category("Подарунок", "income", "🎁"));
        categoryService.addCategory(new Category("Їжа", "expense", "🍔"));
        categoryService.addCategory(new Category("Транспорт", "expense", "🚗"));
        categoryService.addCategory(new Category("Розваги", "expense", "🎮"));
        categoryService.addCategory(new Category("Медицина", "expense", "💊"));
    }
}
