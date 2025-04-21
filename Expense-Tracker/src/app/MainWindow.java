package app;

import controller.AddTransactionDialog;
import controller.StatsDialog;
import controller.LimitManagerDialog;
import model.Transaction;
import model.Category;
import service.TransactionService;
import service.ReportService;
import service.FileService;
import service.CurrencyService;
import service.BudgetService;
import service.CategoryService;
import service.CategoryLimitService;
import service.TimeLimitService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MainWindow extends JFrame {
    private JTable table;
    private JLabel incomeLabel, expenseLabel, balanceLabel, limitLabel;
    private JTextArea currencyArea;
    private JProgressBar limitProgress;

    private final TransactionService transactionService = new TransactionService();
    private final ReportService reportService = new ReportService();
    private final FileService fileService = new FileService();
    private final CurrencyService currencyService = new CurrencyService();
    private final BudgetService budgetService = new BudgetService();
    private final CategoryService categoryService = new CategoryService();
    private final CategoryLimitService categoryLimitService = new CategoryLimitService();
    private final TimeLimitService timeLimitService = new TimeLimitService();

    private final DecimalFormat df = new DecimalFormat("0.00");

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
        JButton limitSettingsBtn = new JButton("Налаштування лімітів");

        topPanel.add(addBtn);
        topPanel.add(statsBtn);
        topPanel.add(saveBtn);
        topPanel.add(loadBtn);
        topPanel.add(limitSettingsBtn);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        JPanel summary = new JPanel(new FlowLayout(FlowLayout.LEFT));
        incomeLabel = new JLabel();
        expenseLabel = new JLabel();
        balanceLabel = new JLabel();
        limitLabel = new JLabel();
        limitProgress = new JProgressBar(0, 100);
        limitProgress.setStringPainted(true);

        summary.add(incomeLabel);
        summary.add(expenseLabel);
        summary.add(balanceLabel);
        summary.add(limitLabel);
        summary.add(limitProgress);

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

        JPopupMenu popup = new JPopupMenu();
        JMenuItem edit = new JMenuItem("Редагувати");
        JMenuItem delete = new JMenuItem("Видалити");
        popup.add(edit);
        popup.add(delete);
        table.setComponentPopupMenu(popup);

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
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Зберегти файл");
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();
                try {
                    if (path.endsWith(".csv")) fileService.saveAsCsv(transactionService.getAllTransactions(), path);
                    else if (path.endsWith(".txt")) fileService.saveAsTxt(transactionService.getAllTransactions(), path);
                    else fileService.saveAsJson(transactionService.getAllTransactions(), path);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Помилка при збереженні файлу.");
                }
            }
        });

        loadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Завантажити JSON файл");
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

        limitSettingsBtn.addActionListener(e -> {
            LimitManagerDialog dialog = new LimitManagerDialog(this, budgetService, categoryLimitService, timeLimitService);
            dialog.setVisible(true);
            updateTable();
        });

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

        new Timer(15000, e -> {
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
                    df.format(t.getAmount()),
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
        int percent = (limit > 0) ? (int) ((expense / limit) * 100) : 0;

        incomeLabel.setText(" Дохід: " + df.format(income) + " грн ");
        expenseLabel.setText(" Витрати: " + df.format(expense) + " грн ");
        balanceLabel.setText(" Баланс: " + df.format(balance) + " грн ");
        limitLabel.setText(" Ліміт: " + df.format(limit) + " грн " + (exceeded ? "(перевищено!)" : ""));
        limitProgress.setValue(Math.min(percent, 100));
    }

    private void updateCurrency() {
        Map<String, Double> rates = currencyService.getAllRates();
        StringBuilder sb = new StringBuilder();

        double usd = rates.getOrDefault("USD", 1.0);
        double eur = rates.getOrDefault("EUR", 1.0);

        sb.append("1 EUR → ").append(df.format(eur)).append(" UAH\n");
        sb.append("1 USD → ").append(df.format(usd)).append(" UAH\n");

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
