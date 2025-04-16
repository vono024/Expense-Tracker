package controller;

import javax.swing.*;
import java.awt.*;

public class AddTransactionDialog extends JDialog {
    private JTextField amountField;
    private JTextField descriptionField;
    private JComboBox<String> categoryBox;
    private JComboBox<String> currencyBox;
    private JComboBox<String> typeBox;
    private JButton saveButton;

    public AddTransactionDialog(JFrame parent) {
        super(parent, "Додати транзакцію", true);
        setSize(400, 300);
        setLayout(new GridLayout(7, 2));

        amountField = new JTextField();
        descriptionField = new JTextField();
        categoryBox = new JComboBox<>(new String[]{"Їжа", "Транспорт", "Розваги"});
        currencyBox = new JComboBox<>(new String[]{"UAH", "USD", "EUR"});
        typeBox = new JComboBox<>(new String[]{"income", "expense"});
        saveButton = new JButton("Зберегти");

        add(new JLabel("Сума:"));
        add(amountField);
        add(new JLabel("Опис:"));
        add(descriptionField);
        add(new JLabel("Категорія:"));
        add(categoryBox);
        add(new JLabel("Валюта:"));
        add(currencyBox);
        add(new JLabel("Тип:"));
        add(typeBox);
        add(saveButton);

        saveButton.addActionListener(e -> dispose());
        setLocationRelativeTo(parent);
    }
}
