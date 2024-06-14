package data;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a category that a recipe can be in. For example vegetarian or indian or smth like that. There can only ever be one instance with a certain name
 */
public final class Category {
    private static final Map<String, Category> INSTANCES = new HashMap<>();

    private String name;

    private Category(String name) {
        this.name = name;
    }

    /**
     * @return The category with the name or creates a new category with the name
     */
    public static Category get(String name) {
        var current = INSTANCES.get(name);
        if (current != null) return current;
        current = new Category(name);
        INSTANCES.put(name, current);
        return current;
    }

    public String getName() {
        return name;
    }

    /**
     * Tries to set the name of this category
     *
     * @throws IllegalArgumentException if there is already another category with that name
     */
    public void setName(String name) {
        if (INSTANCES.containsKey(name))
            throw new IllegalArgumentException("There is already a category with the name " + name);
        INSTANCES.put(this.name, null);
        INSTANCES.put(name, this);
        this.name = name;
    }
}