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
import service.TimeLimitService;
import service.CategoryLimitService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class MainWindow extends JFrame {
    private JTable table;
    private JLabel incomeLabel, expenseLabel, balanceLabel, limitLabel;
    private JTextArea currencyArea;
    private JTextArea limitsTextArea;

    private final TransactionService transactionService = new TransactionService();
    private final ReportService reportService = new ReportService();
    private final FileService fileService = new FileService();
    private final CurrencyService currencyService = new CurrencyService();
    private final BudgetService budgetService = new BudgetService();
    private final CategoryService categoryService = new CategoryService();
    private final TimeLimitService timeLimitService = new TimeLimitService();
    private final CategoryLimitService categoryLimitService = new CategoryLimitService();

    private final DecimalFormat df = new DecimalFormat("0.00");

    public MainWindow() {
        setTitle("Фінансовий трекер");
        setSize(1100, 700);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setExtendedState(JFrame.MAXIMIZED_BOTH);

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

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
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

        limitsTextArea = new JTextArea(12, 30);
        limitsTextArea.setEditable(false);
        JScrollPane limitsScroll = new JScrollPane(limitsTextArea);
        JPanel limitsPanel = new JPanel(new BorderLayout());
        limitsPanel.setBorder(BorderFactory.createTitledBorder("Актуальні ліміти"));
        limitsPanel.add(limitsScroll, BorderLayout.CENTER);

        bottomPanel.add(summary);
        bottomPanel.add(currencyPanel);
        bottomPanel.add(limitsPanel);

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

            chooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV", "csv"));
            chooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("TXT", "txt"));
            chooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON", "json"));

            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();
                String extension = path.substring(path.lastIndexOf(".") + 1); // Отримуємо розширення файлу

                try {
                    switch (extension) {
                        case "csv":
                            fileService.saveAsCsv(transactionService.getAllTransactions(), path);
                            break;
                        case "txt":
                            fileService.saveAsTxt(transactionService.getAllTransactions(), path);
                            break;
                        case "json":
                            fileService.saveAsJson(transactionService.getAllTransactions(), path);
                            break;
                        default:
                            JOptionPane.showMessageDialog(this, "Невідомий формат файлу.");
                            break;
                    }
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
        String[] cols = {"Сума", "Категорія", "Дата", "Опис", "Тип"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Transaction t : list) {
            model.addRow(new Object[]{
                    df.format(t.getAmount()) + " грн",
                    t.getCategory(),
                    t.getDate(),
                    t.getDescription(),
                    t.getType()
            });
        }
        table.setModel(model);
        updateSummary();
        updateCurrency();
        updateLimitsView();
    }

    private void updateSummary() {
        List<Transaction> list = transactionService.getAllTransactions();
        double income = reportService.getTotalByType(list, "income");
        double expense = reportService.getTotalByType(list, "expense");
        double balance = income - expense;

        incomeLabel.setText(" Дохід: " + df.format(income) + " грн ");
        expenseLabel.setText(" Витрати: " + df.format(expense) + " грн ");
        balanceLabel.setText(" Баланс: " + df.format(balance) + " грн ");
    }

    public void updateLimitsView() {
        StringBuilder sb = new StringBuilder();

        double globalLimit = budgetService.getMonthlyLimit();
        double totalExpense = reportService.getTotalByType(transactionService.getAllTransactions(), "expense");
        int globalPercent = (globalLimit > 0) ? (int) ((totalExpense / globalLimit) * 100) : 0;

        if (globalPercent > 100) {
            globalPercent = 100;
            sb.append("Глобальний: ").append(df.format(globalLimit)).append(" грн ").append(globalPercent).append("% Перевищено\n");
        } else {
            sb.append("Глобальний: ").append(df.format(globalLimit)).append(" грн ").append(globalPercent).append("%\n");
        }

        double dailyLimit = timeLimitService.getLimit(TimeLimitService.LimitType.DAILY);
        double dailyExpense = reportService.getTotalByType(transactionService.getAllTransactions(), "expense");
        int dailyPercent = (dailyLimit > 0) ? (int) ((dailyExpense / dailyLimit) * 100) : 0;

        if (dailyPercent > 100) {
            dailyPercent = 100;
            sb.append("Денний: ").append(df.format(dailyLimit)).append(" грн ").append(dailyPercent).append("% Перевищено\n");
        } else {
            sb.append("Денний: ").append(df.format(dailyLimit)).append(" грн ").append(dailyPercent).append("%\n");
        }

        double weeklyLimit = timeLimitService.getLimit(TimeLimitService.LimitType.WEEKLY);
        double weeklyExpense = reportService.getTotalByType(transactionService.getAllTransactions(), "expense");
        int weeklyPercent = (weeklyLimit > 0) ? (int) ((weeklyExpense / weeklyLimit) * 100) : 0;

        if (weeklyPercent > 100) {
            weeklyPercent = 100;
            sb.append("Тижневий: ").append(df.format(weeklyLimit)).append(" грн ").append(weeklyPercent).append("% Перевищено\n");
        } else {
            sb.append("Тижневий: ").append(df.format(weeklyLimit)).append(" грн ").append(weeklyPercent).append("%\n");
        }

        sb.append("Категорії витрат:\n");
        for (Map.Entry<String, Double> entry : categoryLimitService.getAllLimits().entrySet()) {
            double catLimit = entry.getValue();
            double categoryExpense = reportService.getTotalByCategory(transactionService.getAllTransactions(), entry.getKey());
            int categoryPercent = (catLimit > 0) ? (int) ((categoryExpense / catLimit) * 100) : 0;

            if (categoryPercent > 100) {
                categoryPercent = 100;
                sb.append(" - ").append(entry.getKey()).append(": ").append(df.format(catLimit)).append(" грн ").append(categoryPercent).append("% Перевищено\n");
            } else {
                sb.append(" - ").append(entry.getKey()).append(": ").append(df.format(catLimit)).append(" грн ").append(categoryPercent).append("%\n");
            }
        }

        limitsTextArea.setText(sb.toString());
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
        categoryService.addCategory(new Category("Зарплата", "income", ""));
        categoryService.addCategory(new Category("Фріланс", "income", ""));
        categoryService.addCategory(new Category("Подарунок", "income", ""));
        categoryService.addCategory(new Category("Їжа", "expense", ""));
        categoryService.addCategory(new Category("Транспорт", "expense", ""));
        categoryService.addCategory(new Category("Розваги", "expense", ""));
        categoryService.addCategory(new Category("Медицина", "expense", ""));
    }
}
