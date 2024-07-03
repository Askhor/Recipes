package data;

import java.util.HashMap;
import java.util.Map;

public record Ingredient(String name, Einheit einheit) {
    private final static Map<String, Ingredient> ALL_INGREDIENTS = new HashMap<>();

    /**
     * Ensures that is only ever one effective instance that represents an ingredient
     */
    public Ingredient intern() {
        return get(name);
    }

    public static Ingredient get(String name) {
        if (!ALL_INGREDIENTS.containsKey(name)) {
            ALL_INGREDIENTS.put(name, new Ingredient(name, Einheit.G));
        }
        return ALL_INGREDIENTS.get(name);
    }
}