package controller;

import model.Transaction;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class FilterDialog extends JDialog {
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"", "income", "expense"});
    private final JComboBox<String> sortBox = new JComboBox<>(new String[]{"", "Сума ↑", "Сума ↓", "Дата ↑", "Дата ↓"});
    private final JButton applyBtn = new JButton("Застосувати");
    private final JButton resetBtn = new JButton("Скинути");

    private final List<Transaction> originalList;
    private List<Transaction> filteredList = null;

    public FilterDialog(JFrame parent, List<Transaction> transactions) {
        super(parent, "Фільтр", true);
        this.originalList = transactions;

        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(3, 2, 5, 5));

        add(new JLabel("Тип:"));
        add(typeBox);
        add(new JLabel("Сортувати:"));
        add(sortBox);
        add(applyBtn);
        add(resetBtn);

        applyBtn.addActionListener(e -> {
            final String type = (String) typeBox.getSelectedItem();
            final String sort = (String) sortBox.getSelectedItem();

            filteredList = originalList.stream()
                    .filter(t -> type.isEmpty() || t.getType().equals(type))
                    .collect(Collectors.toList());

            switch (sort) {
                case "Сума ↑" -> filteredList.sort(Comparator.comparingDouble(Transaction::getAmount));
                case "Сума ↓" -> filteredList.sort(Comparator.comparingDouble(Transaction::getAmount).reversed());
                case "Дата ↑" -> filteredList.sort(Comparator.comparing(Transaction::getDate));
                case "Дата ↓" -> filteredList.sort(Comparator.comparing(Transaction::getDate).reversed());
            }

            setVisible(false);
        });

        resetBtn.addActionListener(e -> {
            typeBox.setSelectedIndex(0);
            sortBox.setSelectedIndex(0);
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public List<Transaction> getFilteredResults() {
        return filteredList;
    }
}
