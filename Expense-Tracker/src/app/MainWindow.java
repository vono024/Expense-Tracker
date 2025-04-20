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

public class MainWindow extends javax.swing.JFrame {

    private javax.swing.JTable table;
    private javax.swing.JLabel incomeLabel, expenseLabel, balanceLabel, limitLabel;
    private javax.swing.JTextField limitField;
    private javax.swing.JTextArea currencyArea;

    private final service.TransactionService transactionService = new service.TransactionService();
    private final service.ReportService reportService = new service.ReportService();
    private final service.FileService fileService = new service.FileService();
    private final service.CurrencyService currencyService = new service.CurrencyService();
    private final service.BudgetService budgetService = new service.BudgetService();
    private final service.CategoryService categoryService = new service.CategoryService();

    public MainWindow() {
        setTitle("Фінансовий трекер");
        setSize(1100, 700);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new java.awt.BorderLayout());

        initDefaultCategories();

        javax.swing.JPanel topPanel = new javax.swing.JPanel();

        javax.swing.JButton addBtn = new javax.swing.JButton("Додати");
        javax.swing.JButton statsBtn = new javax.swing.JButton("Статистика");
        javax.swing.JButton saveJson = new javax.swing.JButton("Зберегти JSON");
        javax.swing.JButton saveCsv = new javax.swing.JButton("Зберегти CSV");
        javax.swing.JButton saveTxt = new javax.swing.JButton("Зберегти TXT");
        javax.swing.JButton loadBtn = new javax.swing.JButton("Завантажити");
        javax.swing.JButton updateBtn = new javax.swing.JButton("Оновити");

        limitField = new javax.swing.JTextField(6);
        javax.swing.JButton limitBtn = new javax.swing.JButton("Ліміт");

        topPanel.add(addBtn);
        topPanel.add(statsBtn);
        topPanel.add(saveJson);
        topPanel.add(saveCsv);
        topPanel.add(saveTxt);
        topPanel.add(loadBtn);
        topPanel.add(updateBtn);
        topPanel.add(new javax.swing.JLabel("Ліміт:"));
        topPanel.add(limitField);
        topPanel.add(limitBtn);

        table = new javax.swing.JTable();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(table);

        javax.swing.JPanel bottomPanel = new javax.swing.JPanel(new java.awt.GridLayout(2, 1));
        javax.swing.JPanel summary = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        incomeLabel = new javax.swing.JLabel();
        expenseLabel = new javax.swing.JLabel();
        balanceLabel = new javax.swing.JLabel();
        limitLabel = new javax.swing.JLabel();

        summary.add(incomeLabel);
        summary.add(expenseLabel);
        summary.add(balanceLabel);
        summary.add(limitLabel);

        currencyArea = new javax.swing.JTextArea(4, 30);
        currencyArea.setEditable(false);
        javax.swing.JScrollPane currencyScroll = new javax.swing.JScrollPane(currencyArea);
        javax.swing.JPanel currencyPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        currencyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Курси валют"));
        currencyPanel.add(currencyScroll, java.awt.BorderLayout.CENTER);

        bottomPanel.add(summary);
        bottomPanel.add(currencyPanel);

        add(topPanel, java.awt.BorderLayout.NORTH);
        add(scrollPane, java.awt.BorderLayout.CENTER);
        add(bottomPanel, java.awt.BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            controller.AddTransactionDialog dialog = new controller.AddTransactionDialog(this, transactionService, categoryService, currencyService);
            dialog.setVisible(true);
            updateTable();
        });

        statsBtn.addActionListener(e -> {
            controller.StatsDialog dialog = new controller.StatsDialog(this, transactionService);
            dialog.setVisible(true);
        });

        updateBtn.addActionListener(e -> updateTable());

        saveJson.addActionListener(e -> {
            try {
                fileService.saveAsJson(transactionService.getAllTransactions(), "resources/transactions.json");
            } catch (java.io.IOException ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Помилка при збереженні JSON.");
            }
        });

        saveCsv.addActionListener(e -> {
            try {
                fileService.saveAsCsv(transactionService.getAllTransactions(), "resources/transactions.csv");
            } catch (java.io.IOException ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Помилка при збереженні CSV.");
            }
        });

        saveTxt.addActionListener(e -> {
            try {
                fileService.saveAsTxt(transactionService.getAllTransactions(), "resources/transactions.txt");
            } catch (java.io.IOException ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Помилка при збереженні TXT.");
            }
        });

        loadBtn.addActionListener(e -> {
            try {
                java.util.List<model.Transaction> list = fileService.loadFromJson("resources/transactions.json");
                transactionService.clearTransactions();
                list.forEach(transactionService::addTransaction);
                updateTable();
            } catch (java.io.IOException ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Помилка при завантаженні.");
            }
        });

        limitBtn.addActionListener(e -> {
            try {
                double val = java.lang.Double.parseDouble(limitField.getText());
                budgetService.setMonthlyLimit(val);
                updateSummary();
            } catch (java.lang.Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Некоректне число");
            }
        });

        javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem edit = new javax.swing.JMenuItem("Редагувати");
        javax.swing.JMenuItem delete = new javax.swing.JMenuItem("Видалити");
        popup.add(edit);
        popup.add(delete);
        table.setComponentPopupMenu(popup);

        edit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                model.Transaction t = transactionService.getAllTransactions().get(row);
                controller.AddTransactionDialog dialog = new controller.AddTransactionDialog(this, transactionService, t, categoryService, currencyService);
                dialog.setVisible(true);
                updateTable();
            }
        });

        delete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                model.Transaction t = transactionService.getAllTransactions().get(row);
                int confirm = javax.swing.JOptionPane.showConfirmDialog(this, "Видалити транзакцію?", "Підтвердження", javax.swing.JOptionPane.YES_NO_OPTION);
                if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                    transactionService.removeTransaction(t);
                    updateTable();
                }
            }
        });

        updateTable();

        new javax.swing.Timer(15000, e -> {
            currencyService.fetchRatesFromInternet();
            updateCurrency();
            System.out.println("💱 Курси валют автоматично оновлено: " + java.time.LocalTime.now());
            System.out.println("💲 Поточні: \n" + currencyArea.getText());
        }).start();
    }

    private void updateTable() {
        java.util.List<model.Transaction> list = transactionService.getAllTransactions();
        java.lang.String[] cols = {"Сума", "Категорія", "Дата", "Опис", "Валюта", "Тип"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0);
        for (model.Transaction t : list) {
            model.addRow(new java.lang.Object[]{
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
        java.util.List<model.Transaction> list = transactionService.getAllTransactions();
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
        java.util.Map<java.lang.String, java.lang.Double> rates = currencyService.getAllRates();
        java.lang.StringBuilder sb = new java.lang.StringBuilder();

        double uah = rates.getOrDefault("UAH", 1.0);
        double eur = rates.getOrDefault("EUR", 1.0);
        double usd = rates.getOrDefault("USD", 1.0);

        double eurToUah = uah / eur;
        double usdToUah = uah / usd;

        sb.append("1 EUR → ").append(String.format("%.2f", eurToUah)).append(" UAH\n");
        sb.append("1 USD → ").append(String.format("%.2f", usdToUah)).append(" UAH\n");

        currencyArea.setText(sb.toString());
    }

    private void initDefaultCategories() {
        categoryService.addCategory(new model.Category("Зарплата", "income", "💰"));
        categoryService.addCategory(new model.Category("Фріланс", "income", "🧑‍💻"));
        categoryService.addCategory(new model.Category("Подарунок", "income", "🎁"));
        categoryService.addCategory(new model.Category("Їжа", "expense", "🍔"));
        categoryService.addCategory(new model.Category("Транспорт", "expense", "🚗"));
        categoryService.addCategory(new model.Category("Розваги", "expense", "🎮"));
        categoryService.addCategory(new model.Category("Медицина", "expense", "💊"));
    }
}
