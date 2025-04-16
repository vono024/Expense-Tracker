package controller;

import model.Transaction;
import service.ReportService;
import service.TransactionService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class StatsDialog extends JDialog {
    private final JTextArea output;

    public StatsDialog(JFrame parent, TransactionService transactionService) {
        super(parent, "Статистика", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        output = new JTextArea();
        output.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        generateReport(transactionService);
    }

    private void generateReport(TransactionService transactionService) {
        ReportService reportService = new ReportService();
        List<Transaction> all = transactionService.getAllTransactions();

        double totalIncome = reportService.getTotalByType(all, "income");
        double totalExpense = reportService.getTotalByType(all, "expense");
        double balance = reportService.getBalance(all);
        double avgIncome = reportService.getAverageTransaction(all, "income");
        double avgExpense = reportService.getAverageTransaction(all, "expense");

        Map<String, Double> categorySums = reportService.getGroupedCategoryTotals(all, "expense");
        List<Transaction> topExpenses = reportService.getTopTransactions(all, "expense", 3);

        StringBuilder sb = new StringBuilder();
        sb.append("📊 Загальний дохід: ").append(totalIncome).append(" грн\n");
        sb.append("📉 Загальні витрати: ").append(totalExpense).append(" грн\n");
        sb.append("💰 Баланс: ").append(balance).append(" грн\n\n");

        sb.append("📈 Середній дохід: ").append(avgIncome).append(" грн\n");
        sb.append("📉 Середня витрата: ").append(avgExpense).append(" грн\n\n");

        sb.append("🧾 Витрати по категоріях:\n");
        for (Map.Entry<String, Double> entry : categorySums.entrySet()) {
            sb.append(" - ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" грн\n");
        }

        sb.append("\n🔥 Топ витрат:\n");
        for (Transaction t : topExpenses) {
            sb.append(" - ").append(t.getDate()).append(" — ")
                    .append(t.getAmount()).append(" грн (").append(t.getCategory()).append(" — ").append(t.getDescription()).append(")\n");
        }

        output.setText(sb.toString());
    }
}
