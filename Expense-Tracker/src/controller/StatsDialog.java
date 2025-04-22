package controller;

import model.Transaction;
import service.ReportService;
import service.TransactionService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class StatsDialog extends JDialog {
    private final JTextArea output;

    public StatsDialog(JFrame parent, TransactionService transactionService) {
        super(parent, "Статистика", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        output = new JTextArea();
        output.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        output.setEditable(false);

        generateReport(transactionService);

        JScrollPane textScroll = new JScrollPane(output);

        Map<String, Double> categorySums = new ReportService().getGroupedCategoryTotals(transactionService.getAllTransactions(), "expense");
        DefaultPieDataset dataset = new DefaultPieDataset();
        categorySums.forEach(dataset::setValue);

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Витрати по категоріях",
                dataset,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(600, 300));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textScroll, chartPanel);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);
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
        sb.append("Загальний дохід: ").append(totalIncome).append(" грн\n");
        sb.append("Загальні витрати: ").append(totalExpense).append(" грн\n");
        sb.append("Баланс: ").append(balance).append(" грн\n\n");

        sb.append("Середній дохід: ").append(avgIncome).append(" грн\n");
        sb.append("Середня витрата: ").append(avgExpense).append(" грн\n\n");

        sb.append("Витрати по категоріях:\n");
        for (Map.Entry<String, Double> entry : categorySums.entrySet()) {
            sb.append(" - ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" грн\n");
        }

        sb.append("\nТоп витрат:\n");
        for (Transaction t : topExpenses) {
            sb.append(" - ").append(t.getDate()).append(" — ")
                    .append(t.getAmount()).append(" грн (").append(t.getCategory()).append(" — ").append(t.getDescription()).append(")\n");
        }

        output.setText(sb.toString());
    }
}
