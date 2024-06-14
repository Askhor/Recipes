package data;

import java.util.*;

public record Ingredient(String name, Unit unit) {
    private final static Map<String, Ingredient> allIngredients = new HashMap<>();

    /**
     * Ensures that is only ever one effective instance that represents an ingredient
     * */
    public Ingredient intern() {
        if (!allIngredients.containsKey(name)) {
            allIngredients.put(name, this);
        }
        return allIngredients.get(name);
    }
}