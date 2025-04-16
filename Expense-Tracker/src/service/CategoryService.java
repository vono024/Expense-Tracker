package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Category;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {
    private final List<Category> categories = new ArrayList<>();
    private final Gson gson = new Gson();

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void removeCategory(String name) {
        categories.removeIf(c -> c.getName().equalsIgnoreCase(name));
    }

    public void updateCategory(String oldName, Category newCategory) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getName().equalsIgnoreCase(oldName)) {
                categories.set(i, newCategory);
                break;
            }
        }
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }

    public void loadFromJson(String path) throws IOException {
        try (FileReader reader = new FileReader(path)) {
            Type listType = new TypeToken<List<Category>>() {}.getType();
            List<Category> loaded = gson.fromJson(reader, listType);
            categories.clear();
            categories.addAll(loaded);
        }
    }

    public void saveToJson(String path) throws IOException {
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(categories, writer);
        }
    }

    public boolean exists(String name) {
        return categories.stream().anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }
}
