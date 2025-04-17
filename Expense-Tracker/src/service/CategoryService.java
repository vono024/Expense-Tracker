package service;

import model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {
    private final List<Category> categories = new ArrayList<>();

    public void addCategory(Category category) {
        categories.add(category);
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }

    public boolean exists(String name) {
        return categories.stream().anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }
}
