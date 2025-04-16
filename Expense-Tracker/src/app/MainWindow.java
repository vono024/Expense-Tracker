package app;

import controller.AddTransactionDialog;
import controller.StatsDialog;
import model.Transaction;
import service.CurrencyService;
import service.FileService;
import service.ReportService;
import service.TransactionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainWindow extends JFrame {
    private JTable transactionTable;
    private JLabel incomeLabel, expenseLabel, balanceLabel, limitLabel;
    private JTextField limitField;
    private JTextArea currencyArea;

    private TransactionService transactionService = new TransactionService();
    private ReportService reportService = new ReportService();
    private CurrencyService currencyService = new CurrencyService();
    private FileService fileService = new FileService();

    private double monthlyLimit = 0;

    public MainWindow() {
        setTitle("Фінансовий Трекер");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));

        JPanel topPanel = new JPanel();
        JButton addButton = new JButton("Додати");
        JButton statsButton = new JButton("Статистика");
        JButton refreshButton = new JButton("Оновити");
        JButton saveButton = new JButton("Зберегти");
        JButton loadButton = new JButton("Завантажити");

        limitField = new JTextField(6);
        JButton setLimitButton = new JButton("Встановити ліміт");

        topPanel.add(addButton);
        topPanel.add(statsButton);
        topPanel.add(refreshButton);
        topPanel.add(saveButton);
        topPanel.add(loadButton);
        topPanel.add(new JLabel("Ліміт:"));
        topPanel.add(limitField);
        topPanel.add(setLimitButton);

        transactionTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(transactionTable);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        incomeLabel = new JLabel();
        expenseLabel = new JLabel();
        balanceLabel = new JLabel();
        limitLabel = new JLabel();
        summaryPanel.add(incomeLabel);
        summaryPanel.add(expenseLabel);
        summaryPanel.add(balanceLabel);
        summaryPanel.add(limitLabel);

        JPanel currencyPanel = new JPanel(new BorderLayout());
        currencyArea = new JTextArea(4, 30);
        currencyArea.setEditable(false);
        currencyPanel.setBorder(BorderFactory.createTitledBorder("Курси валют"));
        currencyPanel.add(new JScrollPane(currencyArea), BorderLayout.CENTER);

        bottomPanel.add(summaryPanel);
        bottomPanel.add(currencyPanel);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            AddTransactionDialog dialog = new AddTransactionDialog(this, transactionService);
            dialog.setVisible(true);
            updateTable();
        });

        statsButton.addActionListener(e -> {
            StatsDialog dialog = new StatsDialog(this, transactionService);
            dialog.setVisible(true);
        });

        refreshButton.addActionListener(e -> updateTable());

        setLimitButton.addActionListener(e -> {
            try {
                monthlyLimit = Double.parseDouble(limitField.getText());
                updateSummary();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Невірне значення ліміту.");
            }
        });

        saveButton.addActionListener(e -> {
            try {
                fileService.saveAsJson(transactionService.getAllTransactions(), "transactions.json");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Помилка при збереженні.");
            }
        });

        loadButton.addActionListener(e -> {
            try {
                List<Transaction> loaded = fileService.loadFromJson("transactions.json");
                transactionService.clearTransactions();
                loaded.forEach(transactionService::addTransaction);
                updateTable();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Помилка при завантаженні.");
            }
        });

        JPopupMenu popup = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Редагувати");
        JMenuItem deleteItem = new JMenuItem("Видалити");
        popup.add(editItem);
        popup.add(deleteItem);
        transactionTable.setComponentPopupMenu(popup);

        editItem.addActionListener(e -> {
            int row = transactionTable.getSelectedRow();
            if (row != -1) {
                Transaction selected = transactionService.getAllTransactions().get(row);
                AddTransactionDialog dialog = new AddTransactionDialog(this, transactionService, selected);
                dialog.setVisible(true);
                updateTable();
            }
        });

        deleteItem.addActionListener(e -> {
            int row = transactionTable.getSelectedRow();
            if (row != -1) {
                Transaction selected = transactionService.getAllTransactions().get(row);
                int confirm = JOptionPane.showConfirmDialog(this, "Видалити обрану транзакцію?", "Підтвердження", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    transactionService.removeTransaction(selected);
                    updateTable();
                }
            }
        });

        updateTable();
    }

    public void updateTable() {
        List<Transaction> list = transactionService.getAllTransactions();
        String[] columns = {"Сума", "Категорія", "Дата", "Опис", "Валюта", "Тип"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
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
        transactionTable.setModel(model);
        updateSummary();
        updateCurrency();
    }

    private void updateSummary() {
        List<Transaction> list = transactionService.getAllTransactions();
        double income = reportService.getTotalByType(list, "income");
        double expense = reportService.getTotalByType(list, "expense");
        double balance = income - expense;

        incomeLabel.setText(" Дохід: " + income + " грн ");
        expenseLabel.setText(" Витрати: " + expense + " грн ");
        balanceLabel.setText(" Баланс: " + balance + " грн ");
        limitLabel.setText(" Ліміт: " + monthlyLimit + " грн " +
                (expense > monthlyLimit ? "(Перевищено!)" : ""));
    }

    private void updateCurrency() {
        Map<String, Double> map = currencyService.getAllRates();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(" → UAH: ").append(entry.getValue()).append("\n");
        }
        currencyArea.setText(sb.toString());
    }
}
