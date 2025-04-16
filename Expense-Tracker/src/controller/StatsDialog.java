package controller;

import model.Transaction;
import service.ReportService;
import service.TransactionService;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class StatsDialog extends JDialog {
    private JTextArea statsArea;

    public StatsDialog(JFrame parent, TransactionService service) {
        super(parent, "Статистика", true);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(new JScrollPane(statsArea), BorderLayout.CENTER);

        ReportService reportService = new ReportService();
        java.util.List<Transaction> list = service.getAllTransactions();

        double income = reportService.getTotalByType(list, "income");
        double expense = reportService.getTotalByType(list, "expense");
        Map<String, Double> byCategory = reportService.getTotalByCategory(list, "expense");

        StringBuilder sb = new StringBuilder();
        sb.append("📊 Загальний дохід: ").append(income).append("\n");
        sb.append("📉 Загальні витрати: ").append(expense).append("\n\n");
        sb.append("🧾 Витрати по категоріях:\n");

        for (Map.Entry<String, Double> entry : byCategory.entrySet()) {
            sb.append(" - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        statsArea.setText(sb.toString());
    }
}
