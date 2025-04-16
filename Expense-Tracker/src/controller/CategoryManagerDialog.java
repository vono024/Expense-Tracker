package controller;

import model.Category;
import service.CategoryService;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class CategoryManagerDialog extends JDialog {
    private final CategoryService categoryService;
    private final DefaultListModel<Category> listModel = new DefaultListModel<>();
    private final JList<Category> categoryList = new JList<>(listModel);

    public CategoryManagerDialog(JFrame parent, CategoryService service) {
        super(parent, "Керування категоріями", true);
        this.categoryService = service;
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JButton addButton = new JButton("Додати");
        JButton editButton = new JButton("Редагувати");
        JButton deleteButton = new JButton("Видалити");
        JButton saveButton = new JButton("Зберегти");
        JButton loadButton = new JButton("Завантажити");

        JPanel topPanel = new JPanel();
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(saveButton);
        topPanel.add(loadButton);

        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(categoryList);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        refreshList();

        addButton.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField typeField = new JTextField("expense");
            JTextField iconField = new JTextField();
            Object[] fields = {
                    "Назва:", nameField,
                    "Тип (income/expense):", typeField,
                    "Іконка:", iconField
            };
            int result = JOptionPane.showConfirmDialog(this, fields, "Нова категорія", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Category category = new Category(nameField.getText(), typeField.getText(), iconField.getText());
                if (!categoryService.exists(category.getName())) {
                    categoryService.addCategory(category);
                    refreshList();
                } else {
                    JOptionPane.showMessageDialog(this, "Категорія з такою назвою вже існує.");
                }
            }
        });

        editButton.addActionListener(e -> {
            Category selected = categoryList.getSelectedValue();
            if (selected != null) {
                JTextField nameField = new JTextField(selected.getName());
                JTextField typeField = new JTextField(selected.getType());
                JTextField iconField = new JTextField(selected.getIcon());
                Object[] fields = {
                        "Назва:", nameField,
                        "Тип:", typeField,
                        "Іконка:", iconField
                };
                int result = JOptionPane.showConfirmDialog(this, fields, "Редагувати категорію", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    categoryService.updateCategory(selected.getName(), new Category(nameField.getText(), typeField.getText(), iconField.getText()));
                    refreshList();
                }
            }
        });

        deleteButton.addActionListener(e -> {
            Category selected = categoryList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Видалити категорію " + selected.getName() + "?", "Підтвердження", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    categoryService.removeCategory(selected.getName());
                    refreshList();
                }
            }
        });

        saveButton.addActionListener(e -> {
            try {
                categoryService.saveToJson("resources/categories.json");
                JOptionPane.showMessageDialog(this, "Збережено.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Помилка при збереженні.");
            }
        });

        loadButton.addActionListener(e -> {
            try {
                categoryService.loadFromJson("resources/categories.json");
                refreshList();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Помилка при завантаженні.");
            }
        });
    }

    private void refreshList() {
        listModel.clear();
        for (Category c : categoryService.getAllCategories()) {
            listModel.addElement(c);
        }
    }
}
