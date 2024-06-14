package data;

import java.util.HashMap;
import java.util.Map;

public record Ingredient(String name, Unit unit) {
    private final static Map<String, Ingredient> ALL_INGREDIENTS = new HashMap<>();

    /**
     * Ensures that is only ever one effective instance that represents an ingredient
     */
    public Ingredient intern() {
        if (!ALL_INGREDIENTS.containsKey(name)) {
            ALL_INGREDIENTS.put(name, this);
        }
        return ALL_INGREDIENTS.get(name);
    }
}