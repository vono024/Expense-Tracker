package controller;

import service.BudgetService;
import service.CategoryLimitService;
import service.TimeLimitService;
import service.TimeLimitService.LimitType;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class LimitManagerDialog extends JDialog {

    private final JTextField globalLimitField = new JTextField(10);
    private final JTextField dailyLimitField = new JTextField(10);
    private final JTextField weeklyLimitField = new JTextField(10);
    private final JComboBox<String> categoryCombo = new JComboBox<>();
    private final JTextField categoryLimitField = new JTextField(10);

    private final BudgetService budgetService;
    private final CategoryLimitService categoryLimitService;
    private final TimeLimitService timeLimitService;

    public LimitManagerDialog(JFrame parent,
                              BudgetService budgetService,
                              CategoryLimitService categoryLimitService,
                              TimeLimitService timeLimitService) {
        super(parent, "Налаштування лімітів", true);
        this.budgetService = budgetService;
        this.categoryLimitService = categoryLimitService;
        this.timeLimitService = timeLimitService;

        setSize(600, 300);
        setLocationRelativeTo(parent);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fieldsPanel.add(new JLabel("Глобальний ліміт:"));
        fieldsPanel.add(globalLimitField);

        fieldsPanel.add(new JLabel("Денний ліміт:"));
        fieldsPanel.add(dailyLimitField);

        fieldsPanel.add(new JLabel("Тижневий ліміт:"));
        fieldsPanel.add(weeklyLimitField);

        fieldsPanel.add(new JLabel("Категорія витрат:"));
        fieldsPanel.add(categoryCombo);

        fieldsPanel.add(new JLabel("Ліміт категорії:"));
        fieldsPanel.add(categoryLimitField);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Зберегти");
        JButton clearBtn = new JButton("Очистити всі");

        buttonPanel.add(saveBtn);
        buttonPanel.add(clearBtn);

        add(fieldsPanel);
        add(buttonPanel);

        saveBtn.addActionListener(e -> {
            try {
                if (!globalLimitField.getText().isEmpty()) {
                    double global = Double.parseDouble(globalLimitField.getText());
                    budgetService.setMonthlyLimit(global);
                }

                if (!dailyLimitField.getText().isEmpty()) {
                    double daily = Double.parseDouble(dailyLimitField.getText());
                    timeLimitService.setLimit(LimitType.DAILY, daily);
                }

                if (!weeklyLimitField.getText().isEmpty()) {
                    double weekly = Double.parseDouble(weeklyLimitField.getText());
                    timeLimitService.setLimit(LimitType.WEEKLY, weekly);
                }

                String selectedCat = (String) categoryCombo.getSelectedItem();
                if (selectedCat != null && !selectedCat.isEmpty() && !categoryLimitField.getText().isEmpty()) {
                    double catLimit = Double.parseDouble(categoryLimitField.getText());
                    categoryLimitService.setLimit(selectedCat, catLimit);
                }

                ((MainWindow) getParent()).updateLimitsView();

                JOptionPane.showMessageDialog(this, "Ліміти збережено.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Некоректні значення.");
            }
        });

        clearBtn.addActionListener(e -> {
            budgetService.clear();
            timeLimitService.clear();
            categoryLimitService.clear();

            ((MainWindow) getParent()).updateLimitsView();

            JOptionPane.showMessageDialog(this, "Усі ліміти очищено.");
        });

        updateCategoryCombo();
        setDecimalInputFilter(globalLimitField);
        setDecimalInputFilter(dailyLimitField);
        setDecimalInputFilter(weeklyLimitField);
        setDecimalInputFilter(categoryLimitField);
    }

    private void updateCategoryCombo() {
        categoryCombo.removeAllItems();
        String[] expenseCategories = {"Їжа", "Транспорт", "Розваги", "Медицина"};
        for (String cat : expenseCategories) categoryCombo.addItem(cat);
    }

    private void setDecimalInputFilter(JTextField field) {
        field.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
                String text = getText(0, getLength());
                String newText = text.substring(0, offset) + str + text.substring(offset);
                if (newText.matches("\\d{0,10}(\\.\\d{0,2})?")) {
                    super.insertString(offset, str, a);
                }
            }
        });
    }
}
