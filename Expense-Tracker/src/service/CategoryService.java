package service;

import model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {
    private final List<Category> categories = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();

    public void addCategory(Category category) {
        categories.add(category);
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }

    public List<Category> getAll() {
        return categoryList;
    }
}
