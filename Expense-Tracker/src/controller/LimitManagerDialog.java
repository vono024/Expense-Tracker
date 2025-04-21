package controller;

import app.MainWindow;
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

        setSize(400, 300); // Зменшуємо розмір вікна
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 5, 5));  // Оновлюємо кількість рядків до 6
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

        JButton saveBtn = new JButton("Зберегти");
        JButton clearBtn = new JButton("Очистити всі");

        fieldsPanel.add(saveBtn);
        fieldsPanel.add(clearBtn);

        add(fieldsPanel, BorderLayout.CENTER); // Оновлюємо, щоб тільки ці поля відображались

        // Збереження лімітів в LimitManagerDialog
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

                // Після збереження лімітів оновлюємо їх на головному екрані
                ((MainWindow) getParent()).updateLimitsView();

                JOptionPane.showMessageDialog(this, "Ліміти збережено.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Некоректні значення.");
            }
        });

        clearBtn.addActionListener(e -> {
            budgetService.clear();
            timeLimitService.clear();  // очищення лімітів часу
            categoryLimitService.clear();  // очищення лімітів категорій
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
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                String text = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = text.substring(0, offset) + string + text.substring(offset);
                if (isValidInput(newText)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (isValidInput(newText)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean isValidInput(String text) {
                return text.matches("\\d{0,10}(\\.\\d{0,2})?"); // Правило для чисел з двома знаками після коми
            }
        });
    }
}
