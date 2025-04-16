package app;

import controller.AddTransactionDialog;
import controller.StatsDialog;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JButton addButton;
    private JButton statsButton;
    private JTable transactionTable;

    public MainWindow() {
        setTitle("Трекер особистих фінансів");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        addButton = new JButton("Додати");
        statsButton = new JButton("Статистика");

        addButton.addActionListener(e -> openAddTransaction());
        statsButton.addActionListener(e -> openStats());

        JPanel topPanel = new JPanel();
        topPanel.add(addButton);
        topPanel.add(statsButton);

        transactionTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(transactionTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void openAddTransaction() {
        AddTransactionDialog dialog = new AddTransactionDialog(this);
        dialog.setVisible(true);
    }

    private void openStats() {
        StatsDialog dialog = new StatsDialog(this);
        dialog.setVisible(true);
    }
}
