package controller;

import javax.swing.*;

public class StatsDialog extends JDialog {
    public StatsDialog(JFrame parent) {
        super(parent, "Статистика", true);
        setSize(400, 300);
        add(new JLabel("Тут буде статистика витрат і доходів"), SwingConstants.CENTER);
        setLocationRelativeTo(parent);
    }
}
