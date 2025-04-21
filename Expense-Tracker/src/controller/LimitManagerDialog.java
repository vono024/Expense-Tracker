package controller;

import service.BudgetService;
import service.CategoryLimitService;
import service.TimeLimitService;
import service.TimeLimitService.LimitType;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LimitManagerDialog extends JDialog {
    private final JTextField globalLimitField = new JTextField(10);
    private final JTextField dailyLimitField = new JTextField(10);
    private final JTextField weeklyLimitField = new JTextField(10);
    private final JComboBox<String> categoryCombo = new JComboBox<>();
    private final JTextField categoryLimitField = new JTextField(10);
    private final JTextArea currentLimitsArea = new JTextArea(10, 30);

    private final BudgetService budgetService;
    private final CategoryLimitService categoryLimitService;
    private final TimeLimitService timeLimitService;

    public LimitManagerDialog(JFrame parent, BudgetService budgetService,
                              CategoryLimitService categoryLimitService,
                              TimeLimitService timeLimitService) {
        super(parent, "Налаштування лімітів", true);
        this.budgetService = budgetService;
        this.categoryLimitService = categoryLimitService;
        this.timeLimitService = timeLimitService;

        setSize(500, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fieldsPanel.add(new JLabel("Глобальний ліміт:"));
        fieldsPanel.add(globalLimitField);

        fieldsPanel.add(new JLabel("Денний ліміт:"));
        fieldsPanel.add(dailyLimitField);

        fieldsPanel.add(new JLabel("Тижневий ліміт:"));
        fieldsPanel.add(weeklyLimitField);

        fieldsPanel.add(new JLabel("Категорія:"));
        fieldsPanel.add(categoryCombo);

        fieldsPanel.add(new JLabel("Ліміт категорії:"));
        fieldsPanel.add(categoryLimitField);

        JButton saveBtn = new JButton("Зберегти");
        JButton refreshBtn = new JButton("Оновити список");
        fieldsPanel.add(refreshBtn);
        fieldsPanel.add(saveBtn);

        currentLimitsArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(currentLimitsArea);

        add(fieldsPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        saveBtn.addActionListener(e -> {
            try {
                double global = Double.parseDouble(globalLimitField.getText());
                double daily = Double.parseDouble(dailyLimitField.getText());
                double weekly = Double.parseDouble(weeklyLimitField.getText());

                budgetService.setMonthlyLimit(global);
                timeLimitService.setLimit(LimitType.DAILY, daily);
                timeLimitService.setLimit(LimitType.WEEKLY, weekly);

                String selectedCat = (String) categoryCombo.getSelectedItem();
                double catLimit = Double.parseDouble(categoryLimitField.getText());
                if (selectedCat != null && !selectedCat.isEmpty()) {
                    categoryLimitService.setLimit(selectedCat, catLimit);
                }

                JOptionPane.showMessageDialog(this, "Ліміти збережено.");
                updateLimitsView();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Некоректне значення.");
            }
        });

        refreshBtn.addActionListener(e -> updateLimitsView());

        updateCategoryCombo();
        updateLimitsView();
    }

    private void updateCategoryCombo() {
        categoryCombo.removeAllItems();
        String[] basic = new String[]{"Їжа", "Транспорт", "Розваги", "Медицина", "Зарплата", "Фріланс", "Подарунок"};
        for (String c : basic) categoryCombo.addItem(c);
    }

    private void updateLimitsView() {
        StringBuilder sb = new StringBuilder();
        sb.append("🌐 Глобальний: ").append(budgetService.getMonthlyLimit()).append(" грн\n");
        sb.append("📅 Денний: ").append(timeLimitService.getLimit(LimitType.DAILY)).append(" грн\n");
        sb.append("📆 Тижневий: ").append(timeLimitService.getLimit(LimitType.WEEKLY)).append(" грн\n");
        sb.append("📂 Категорії:\n");
        for (Map.Entry<String, Double> entry : categoryLimitService.getAllLimits().entrySet()) {
            sb.append("   - ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" грн\n");
        }
        currentLimitsArea.setText(sb.toString());
    }
}
